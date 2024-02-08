package com.systemcraftsman.demo.model;

import java.io.Serializable;

public class ExtendedSensor extends Sensor implements Serializable {
    private float fTemperature;

    public ExtendedSensor() {
        super();
        // Default constructor required for serialization
    }

    public ExtendedSensor(float temperature, float humidity, float wind, float soil, float fTemperature) {
        super(temperature, humidity, wind, soil);
        this.fTemperature = fTemperature;
    }

    public float getfTemperature() {
        return fTemperature;
    }

    public void setfTemperature(float fTemperature) {
        this.fTemperature = fTemperature;
    }

    @Override
    public String toString() {
        return "ExtendedSensor{" +
                "temperature=" + getTemperature() +
                ", humidity=" + getHumidity() +
                ", wind=" + getWind() +
                ", soil=" + getSoil() +
                ", fTemperature=" + fTemperature +
                '}';
    }
}
