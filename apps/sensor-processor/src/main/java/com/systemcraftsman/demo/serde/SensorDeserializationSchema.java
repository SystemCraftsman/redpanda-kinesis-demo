package com.systemcraftsman.demo.serde;

import com.systemcraftsman.demo.model.Sensor;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SensorDeserializationSchema implements DeserializationSchema<Sensor> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Sensor deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, Sensor.class);
    }

    @Override
    public boolean isEndOfStream(Sensor nextElement) {
        return false; // We assume the stream is never ending
    }

    @Override
    public TypeInformation<Sensor> getProducedType() {
        return TypeExtractor.getForClass(Sensor.class);
    }
}
 