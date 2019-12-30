package com.inone.weather.gson;

import com.google.gson.annotations.SerializedName;
/*
* 天气预报
* */
public class Forecast {
    public String date; //日期

    @SerializedName("tmp") //最高最低
    public Temperature temperature;

    @SerializedName("cond")//天气
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
