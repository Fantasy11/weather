package com.zcf.weather.Gson;

import com.google.gson.annotations.SerializedName;

public class Daily_forecast {
    public String date;
    @SerializedName("cond")
    public Detail_d detail_d;
    public Tmp tmp;

    public class Detail_d{
        public String txt_d;
    }
    public  class Tmp{
        public String max;
        public String min;
    }
}
