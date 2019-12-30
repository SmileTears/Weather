package com.inone.weather.gson;

import com.google.gson.annotations.SerializedName;
/*
* 现在天气情况
* */
public class Now {
    @SerializedName("tmp")
    public String temperature;//温度

    @SerializedName("cond")//天气
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
