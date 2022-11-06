package com.example.wechat.service.weather;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WeatherConstants;
import com.example.wechat.entity.WeatherInfo;
import com.example.wechat.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Administrator
 * @date 2022/11/5 23:20
 */
@Slf4j
@Service
public class WeatherService {

    @Value("${weather.key}")
    private String weatherKey;

    @Value("${juhe.weather.key}")
    private String juHeWeatherKey;

    public String findCity(String cityName) {
        String requestUrl = MessageFormat.format(WeatherConstants.LOCATION_URL, cityName, weatherKey);

        String responseStr = OkHttpUtil.getRequest(requestUrl);
        if (StringUtils.hasLength(responseStr)) {
            JSONObject parse = (JSONObject) JSONObject.parse(responseStr);

            JSONArray jsonArray = parse.getJSONArray("location");
            if (!jsonArray.isEmpty()) {
                JSONObject o = (JSONObject) jsonArray.get(0);
                return o.getString("id");
            }
        }
        return null;
    }


    public String getWeather(String cityName) {
        String cityCode = findCity(cityName);
        if (StringUtils.hasLength(cityCode)) {
            String requestUrl = MessageFormat.format(WeatherConstants.WEATHER_URL, cityCode, weatherKey);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(requestUrl).get().build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseStr = Objects.requireNonNull(response.body()).string();
                    log.info(responseStr);
                }
            } catch (IOException e) {
                throw new RuntimeException("获取Access Token 失败");
            }
        }
        return null;
    }


    public WeatherInfo getJuHeWeather(String cityName) {
        WeatherInfo weatherInfo = new WeatherInfo();

        String requestUrl = MessageFormat.format(WeatherConstants.JUHE_WEATHER_URL, cityName, juHeWeatherKey);
        String responseStr = OkHttpUtil.getRequest(requestUrl);
        if (StringUtils.hasLength(responseStr)) {
            JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
            String resultCode = responseObj.getString("resultcode");
            if (WeatherConstants.SUCCESS_FLAG.equals(resultCode)) {
                JSONObject resultObj = responseObj.getJSONObject("result");
                if (resultObj.isEmpty()) {
                    return weatherInfo;
                }

                JSONObject today = resultObj.getJSONObject("today");
                if (today.isEmpty()) {
                    return weatherInfo;
                }

                weatherInfo.setCity(today.getString("city"));
                weatherInfo.setDate(today.getString("date_y") + " " + today.getString("week"));

                weatherInfo.setWeather(today.getString("weather"));
                weatherInfo.setDressAdvice(today.getString("dressing_advice"));

                String temperatureStr = today.getString("temperature");
                if (StringUtils.hasLength(temperatureStr)) {
                    String[] temperatures = temperatureStr.split("~");

                    weatherInfo.setLowTemperature(temperatures[0]);
                    weatherInfo.setHighTemperature(temperatures[1]);
                }
            }
        }
        return weatherInfo;
    }


}
