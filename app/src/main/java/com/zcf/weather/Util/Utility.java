package com.zcf.weather.Util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zcf.weather.Gson.Weather;
import com.zcf.weather.db.City;
import com.zcf.weather.db.County;
import com.zcf.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            JSONArray allProvinces = null;
            try {
                allProvinces = new JSONArray(response);
                for(int i =0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province =new Province();
                    province.setpName(provinceObject.getString("name"));
                    province.setId(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int pId){
        if(!TextUtils.isEmpty(response)){

            try {
                JSONArray allCities = new JSONArray(response);
                for(int i =0;i<allCities.length();i++){
                    JSONObject citiesJSONObject = allCities.getJSONObject(i);
                    City cities =new City();
                    Log.d("ttt--2",citiesJSONObject.getString("name")+" "+citiesJSONObject.getInt("id"));
                    cities.setCityName(citiesJSONObject.getString("name"));
                    cities.setCityCode(citiesJSONObject.getInt("id"));
                    cities.setpId(pId);
                    cities.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cId){
        if(!TextUtils.isEmpty(response)){
            JSONArray allCounty=null;
            try {
                 allCounty = new JSONArray(response);
                for(int i =0;i<allCounty.length();i++){
                    JSONObject countyJSONObject = allCounty.getJSONObject(i);
                    County county =new County();
                    county.setCountyName(countyJSONObject.getString("name"));
                    //county.setId(countyJSONObject.getInt("id"));
                    county.setWeatherId(countyJSONObject.getString("weather_id"));
                    county.setCityId(cId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject =new JSONObject(response);
            JSONArray jsonArray =jsonObject.getJSONArray("HeWeather");
            String weatherContent =jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
