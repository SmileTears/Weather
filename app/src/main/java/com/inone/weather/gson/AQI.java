package com.inone.weather.gson;
/*
* aqi 和pm25
* */
public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
