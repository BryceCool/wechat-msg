package com.example.wechat.constant;

/**
 * @author Administrator
 * @date 2022/11/5 23:22
 */
public class WeatherConstants {

    public static final String SUCCESS_FLAG = "200";

    /**
     * 请求位置的URL
     * 1. location 必填 需要查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标
     * eg： location=北京 或 location=116.41,39.92
     */
    public static final String LOCATION_URL = "https://geoapi.qweather.com/v2/city/lookup?location={0}&key={1}";


    /**
     * 获取天气信息
     */
    public static final String WEATHER_URL = "https://devapi.qweather.com/v7/weather/now?location={0}&key={1}";



    public static final String JUHE_WEATHER_URL = "http://v.juhe.cn/weather/index?format=2&cityname={0}&key={1}";
}
