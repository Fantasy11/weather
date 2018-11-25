package com.zcf.weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zcf.weather.Gson.Daily_forecast;
import com.zcf.weather.Gson.Weather;
import com.zcf.weather.Util.HttpUtil;
import com.zcf.weather.Util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private ScrollView scrollView;
    private TextView tvtitleCity,tvtitleUpdateTime,tvtemp,tvweatherdetail;
    private LinearLayout futureItem;
    private TextView tvaqi,tvpm25,tvcomfort,tvwashCar,tvsport;
    private ImageView bgPic;
    public SwipeRefreshLayout swipeRefreshLayout;
    private ImageView home_bt;
    public DrawerLayout drawerLayout;
    private String weatherId,rPicUrl;
    private static String Tag="xxxx";
    private static final boolean DEBUG = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView =getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        scrollView = findViewById(R.id.layout_weather);
        tvtitleCity = findViewById(R.id.title_city);
        tvtitleUpdateTime =findViewById(R.id.tv_updatTime);
        tvtemp =findViewById(R.id.tv_Temp);
        tvweatherdetail=findViewById(R.id.tv_weather_detail);
        futureItem = findViewById(R.id.ll_future);
        tvaqi = findViewById(R.id.tv_aqi);
        tvpm25 = findViewById(R.id.tv_pm25);
        tvcomfort = findViewById(R.id.tv_comfort);
        tvwashCar = findViewById(R.id.tv_washCar);
        tvsport = findViewById(R.id.tv_sport);
        bgPic=findViewById(R.id.background_pic);
        swipeRefreshLayout=findViewById(R.id.sRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        home_bt=findViewById(R.id.home_bt);
        home_bt.setOnClickListener(this);
        drawerLayout=findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawers();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);
        rPicUrl =sharedPreferences.getString("pic",null);

        if(rPicUrl!=null){
            Glide.with(this).load(rPicUrl).into(bgPic);
        }
        else {
            loadGetPicUrl();
        }
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
            weatherId=weather.basic.weatherCode;
        }
        else {
            weatherId = getIntent().getStringExtra("weatherId");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeatherIfo(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeatherIfo(weatherId);
            }
        });
    }

    private void loadGetPicUrl() {
        String url ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this,"无法获取BG",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picUrl = response.body().string();
                if(!picUrl.equals(rPicUrl)){
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("pic",picUrl);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(picUrl).into(bgPic);
                        }
                    });
                }
            }
        });
    }

    public void requestWeatherIfo(String weatherId) {
        loadGetPicUrl();
        String key ="071b52abb0cd4f6dbe8bcc17881aa99c";
        String weatherUrl ="http://guolin.tech/api/weather" +
                "?cityid="+weatherId+"&key="+key;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败b",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String response1 = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(response1);
                if(DEBUG){Log.d(Tag,response1);}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        if(weather!=null &&weather.status.equals("ok")){
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",response1);
                            editor.apply();
                            showWeatherInfo(weather);

                        }
                        else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败a",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updatTime =weather.basic.update.updateTime.split(" ")[1];
        String temp = weather.now.tmp+"℃";
        String detail = weather.now.detail.txt;
        tvtitleCity.setText(cityName);
        tvtitleUpdateTime.setText(updatTime);
        tvtemp.setText(temp);
        tvweatherdetail.setText(detail);
        futureItem.removeAllViews();

        if(DEBUG)Log.d(Tag,temp+" "+detail);

        for(Daily_forecast dailyForecast : weather.daily_forecastList){
            if(DEBUG)Log.d(Tag,dailyForecast.date);

            View view = LayoutInflater.from(this).inflate(R.layout.future_item,
                    futureItem,false);
            TextView tvDate =view.findViewById(R.id.tv_futureDate);
            TextView tvIfo =view.findViewById(R.id.tv_futureDetail);
            TextView tvMax =view.findViewById(R.id.tv_futureMax);
            TextView tvMin =view.findViewById(R.id.tv_futureMin);
            tvDate.setText(dailyForecast.date);
            tvIfo.setText(dailyForecast.detail_d.txt_d);
            String a =dailyForecast.tmp.max+"℃";
            tvMax.setText(a);
            String b =dailyForecast.tmp.min+"℃";
            tvMin.setText(b);
            futureItem.addView(view);
        }
        if(weather.aqi!=null){
            tvaqi.setText(weather.aqi.city.aqi);
            tvpm25.setText(weather.aqi.city.pm25);
        }
        String comfort ="舒适度："+weather.suggestion.comfort.txt;
        String washCar = "洗车指数："+weather.suggestion.carWash.txt;
        String sport = "运动建议："+weather.suggestion.sport.txt;
        tvcomfort.setText(comfort);
        tvwashCar.setText(washCar);
        tvsport.setText(sport);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_bt:
                drawerLayout.openDrawer(GravityCompat.START);
        }

    }
}
