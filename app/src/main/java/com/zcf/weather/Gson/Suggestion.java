package com.zcf.weather.Gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    public class Comfort{
        public String txt;
    }
    public class CarWash{
        public String txt;
    }
    public class Sport{
        public String txt;
    }
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;
}
