package com.inone.weather.gson;

import com.google.gson.annotations.SerializedName;

/*
* 基本信息
* */
public class Basic {
    @SerializedName("city") //让JSON字段和Java字段之间家里映射关系
    public String cityName; //城市名字

    @SerializedName("id")
    public String weatherId;//城市对应天气ID

    public Update update;

    public  class Update{
        @SerializedName("loc") //更新时间
        public String updateTime;
    }

}
