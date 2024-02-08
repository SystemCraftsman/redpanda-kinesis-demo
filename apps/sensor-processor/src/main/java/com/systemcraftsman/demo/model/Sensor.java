package com.systemcraftsman.demo.model;

import java.io.Serializable;

public class Sensor implements Serializable {
    private float temperature;
    private float humidity;
    private float wind;
    private float soil;

    public Sensor() {
        // Default constructor required for serialization
    }

    public Sensor(float temperature, float humidity, float wind, float soil) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.soil = soil;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public float getSoil() {
        return soil;
    }

    public void setSoil(float soil) {
        this.soil = soil;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", wind=" + wind +
                ", soil=" + soil +
                '}';
    }
}