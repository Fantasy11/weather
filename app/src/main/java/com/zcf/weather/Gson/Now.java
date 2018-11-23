package com.zcf.weather.Gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    public String tmp;

    @SerializedName("cond")
    public Detail detail;
    public class Detail{
        public String txt;
    }
}
