package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.SchemaType;

/**
 * The schema for an EventType, expected to be a json schema in yaml
 * format (other formats might be added in the future).
 * @param type The type of schema definition (avro, json-schema, etc).
 * @param schema The schema as string in the syntax defined in the field type.
 * Failure to respect the syntax will fail any operation on an EventType.
 */

public class EventTypeSchema {
    private final SchemaType type;
    private final String schema;

    public EventTypeSchema(SchemaType type, String schema) {
        this.type = type;
        this.schema = schema;
    }

    public SchemaType getType() {
        return type;
    }

    public String getSchema() {
        return schema;
    }

}
