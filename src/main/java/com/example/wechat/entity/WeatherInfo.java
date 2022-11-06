package com.example.wechat.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @date 2022/11/6 10:59
 */
@Accessors(chain = true)
@Data
public class WeatherInfo {

    private String city;

    private String date;

    private String lowTemperature;

    private String highTemperature;

    private String weather;

    private String dressAdvice;
}
