package com.systemcraftsman.demo.serde;

import com.systemcraftsman.demo.model.Sensor;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ExtendedSensorSerializationSchema<T extends  Sensor> implements SerializationSchema<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(T t) {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing extended sensor object", e);
        }
    }
}
