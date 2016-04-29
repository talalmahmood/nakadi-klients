package org.zalando.nakadi.client.examples.java;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.EventValidationStrategy;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.enumerator.SchemaType;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.scala.ClientFactory;
import org.zalando.nakadi.client.utils.ClientBuilder;
import org.zalando.nakadi.client.utils.Serialization;

import com.google.common.collect.Lists;

public class EventCreationExample {
	/**
	 * Define how your event should look like
	 */
	public class MeetingsEvent implements Event {
		private final String date;
		private final String topic;

		public MeetingsEvent(String date, String topic) {
			this.date = date;
			this.topic = topic;
		}

		public String getDate() {
			return date;
		}

		public String getTopic() {
			return topic;
		}

	}

	public EventTypeSchema createEventTypeSchema(String schema) {
		return new EventTypeSchema(SchemaType.JSON, schema);
	}

	public EventType createEventType(String name,
			EventTypeSchema eventTypeSchema) {
		String owningApplication = "team-laas";
		EventTypeCategory category = EventTypeCategory.UNDEFINED;
		List<EventValidationStrategy> validationStrategies = Lists
				.newArrayList();
		List<EventEnrichmentStrategy> enrichmentStrategies = Lists
				.newArrayList();
		PartitionStrategy partitionStrategy = PartitionStrategy.RANDOM;

		List<String> dataKeyFields = null;
		List<String> partitionKeyFields = Lists.newArrayList("date", "topic");
		EventTypeStatistics statistics = null;
		return new EventType(name, //
				owningApplication, //
				category, //
				validationStrategies, //
				enrichmentStrategies, //
				partitionStrategy, //
				eventTypeSchema, //
				dataKeyFields, //
				partitionKeyFields, //
				statistics);

	}

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {

		/**
		 * An Event-type is ment for describing: 1. The Type of Events you want
		 * to create (i.e.: BusinessEvent) . 2. Schema validations to be
		 * enforced(or not)by Nakadi 3. How events should be distributed between
		 * their own partitions 4. A unique identifier for Subscribing
		 * 
		 */
		String eventTypeName = "MeetingsEvent-example-E";

		/**
		 * Create client
		 */
		final Client client = new ClientBuilder()//
				.withHost(ClientFactory.host())//
				.withSecuredConnection(true) // s
				.withVerifiedSslCertificate(false) // s
				.withTokenProvider4Java(() -> ClientFactory.getToken())//
				.buildJavaClient();

		/**
		 * nakadi needs to know what kind of Json-schema you are going to send to
		 * the Event. We need to define a schema that matches the Event that we
		 * want to send along(MeetingsEvent).
		 */
		String schema = " { 'properties': { 'date': { 'type': 'string' }, 'topic': { 'type': 'string'} } }"
				.replaceAll("'", "\"");

		EventCreationExample example = new EventCreationExample();
		EventTypeSchema eventTypeSchema = example.createEventTypeSchema(schema);
		EventType eventType = example.createEventType(eventTypeName,
				eventTypeSchema);
		Future<Void> result = null;
		result = client.createEventType(eventType);
		result.get();

		// Create the event
		MeetingsEvent event = example.new MeetingsEvent(
				"2016-04-28T13:28:15+00:00", "Hackaton");
		MeetingsEvent event1 = example.new MeetingsEvent(
				"2016-04-28T13:28:15+00:00", "Hackaton1");
		MeetingsEvent event2 = example.new MeetingsEvent(
				"2016-04-28T13:28:15+00:00", "Hackaton2");
		MeetingsEvent event3 = example.new MeetingsEvent(
				"2016-04-28T13:28:15+00:00", "Hackaton3");
		// Single Event
		result = client.publishEvent(eventTypeName, event);
		result.get();
		// Single Event with Serializer,
		result = client.publishEvent(eventTypeName, event1,
				Serialization.defaultSerializer());
		result.get();
		// Multi Event
		result = client.publishEvents(eventTypeName, Arrays.asList(event2));
		result.get();
		// Multi Event with Serializer
		result = client.publishEvents(eventTypeName, Arrays.asList(event3),
				Serialization.defaultSerializer());
		result.get();

	}
}
