package com.inone.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inone.weather.gson.Forecast;
import com.inone.weather.gson.Weather;
import com.inone.weather.util.HttpUtil;
import com.inone.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    //控件
    private static final String TAG = "WeatherActivity";
    private ScrollView weatherLayout;       //滚动
    private TextView titleCity;             //当前城市
    private TextView titleUpdateTime;       //更新时间
    private TextView degreeText;            //当天气温
    private TextView weatherInfoText;       //当天天气情况
    private LinearLayout forecastLayout;    //未来几天的天气
    private TextView aqiText;       //aqi
    private TextView pm25Text;      //pm25
    private TextView comfortText;   //舒适度
    private TextView carWashText;   //洗车指数
    private TextView sportText;     //运动指数

    //每日一图
    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让状态栏和背景栏融合在一起
        if(Build.VERSION.SDK_INT >= 21){ //5.0以上的系统才支持
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//改变系统UI显示，布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT); //状态栏设置透明色
        }

        setContentView(R.layout.activity_weather);

        //获取控件实例
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText =(TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text= (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        //每日一图
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);


//        //获取刷新对象
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }


        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存直接解析天气
            Weather weather = Utility.handleWeatherResponse(weatherString);

           // mWeatherId =weather.basic.weatherId;

            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器上查询
           // mWeatherId = getIntent().getStringExtra("weather_id");


            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);//从服务器获取天气数据
        }

    }


    /**
     * 根据天气ID请求城市天气信息
     * */

    public void requestWeather(final  String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+ weatherId + "&key=cfb38d666d924f70932e0b0b5ba73f63";
        System.out.println("****************************URl=" + weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败。。。",Toast.LENGTH_SHORT).show();
                       // swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                      //  swipeRefreshLayout.setRefreshing(false); //刷新时间结束，隐藏刷新进度条
                    }
                });
            }
        });
        loadBingPic();
    }




    /*
     * 处理并展示weather实体类中的数据
     * */
    public void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        Log.d(TAG, "showWeatherInfo: weatherInfo=" + weatherInfo);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for(Forecast forecast:weather.forecastList){
            Log.d(TAG, "showWeatherInfo:Forecast=== "  +"******"+forecast);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        carWashText.setText(carWash);
        comfortText.setText(comfort);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

//        Intent intent = new Intent(this,AutoUpdateService.class);
//        startService(intent);

    }

    /*
     * 加载必应每日一图
     * */

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);

                    }
                });
            }
        });

    }




}