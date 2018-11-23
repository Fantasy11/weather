package com.zcf.weather.Gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherCode;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
