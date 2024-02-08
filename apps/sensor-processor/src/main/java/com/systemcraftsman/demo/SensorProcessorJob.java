package com.systemcraftsman.demo;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.systemcraftsman.demo.model.ExtendedSensor;
import com.systemcraftsman.demo.model.Sensor;
import com.systemcraftsman.demo.serde.ExtendedSensorSerializationSchema;
import com.systemcraftsman.demo.serde.SensorDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.Properties;


public class SensorProcessorJob {

    private static DataStream<Sensor> createKafkaSourceFromApplicationProperties(StreamExecutionEnvironment env) throws IOException {
        Properties sourceProperties = KinesisAnalyticsRuntime.getApplicationProperties().get("KafkaSource");

        String jaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                "username=\"" + sourceProperties.getProperty("sasl.username") + "\" " +
                "password=\"" + sourceProperties.getProperty("sasl.password") + "\";";


        KafkaSource<Sensor> source = KafkaSource.<Sensor>builder()
                .setBootstrapServers((String) sourceProperties.get("bootstrap.servers"))
                .setTopics((String) sourceProperties.get("topic"))
                .setGroupId("kafka-replication")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SensorDeserializationSchema())
                .setProperty("sasl.mechanism", (String) sourceProperties.get("sasl.mechanism"))
                .setProperty("security.protocol", (String) sourceProperties.get("security.protocol"))
                .setProperty("sasl.jaas.config", jaasConfig)
                .build();

        return env.fromSource(source, WatermarkStrategy.noWatermarks(), "Sensor Source");
    }

    private static <T extends Sensor> KafkaSink<T> createKafkaSinkFromApplicationProperties() throws IOException {
        Properties sinkProperties = KinesisAnalyticsRuntime.getApplicationProperties().get("KafkaSink");

        String jaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                "username=\"" + sinkProperties.getProperty("sasl.username") + "\" " +
                "password=\"" + sinkProperties.getProperty("sasl.password") + "\";";

        sinkProperties.setProperty("sasl.jaas.config", jaasConfig);

        return KafkaSink.<T>builder()
                .setBootstrapServers(sinkProperties.getProperty("bootstrap.servers"))
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic((String) sinkProperties.get("topic"))
                        .setValueSerializationSchema(new ExtendedSensorSerializationSchema<T>())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setKafkaProducerConfig(sinkProperties)
                .build();
    }

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Sensor> stream = createKafkaSourceFromApplicationProperties(env);

        stream.map((MapFunction<Sensor, ExtendedSensor>) sensor -> {
            float humidity = sensor.getHumidity();
            float soil = sensor.getSoil();
            float wind = sensor.getWind();
            float temperatureCelcius = sensor.getTemperature();

            float temperatureFahrenheit = (temperatureCelcius * 9 / 5) + 32;

            return new ExtendedSensor(temperatureCelcius, humidity, wind, soil, temperatureFahrenheit);
        }).sinkTo(createKafkaSinkFromApplicationProperties());

        env.execute("Sensor Processor Flink Application");
    }
}