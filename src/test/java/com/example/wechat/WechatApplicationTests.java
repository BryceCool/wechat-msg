package com.example.wechat;

import com.example.wechat.entity.WeatherInfo;
import com.example.wechat.service.weather.WeatherService;
import com.example.wechat.service.phrase.PhraseService;
import com.example.wechat.service.wechat.WechatTemplateMsgService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = WechatApplication.class)
class WechatApplicationTests {

    @Resource
    private WechatTemplateMsgService wechatTemplateMsgService;

    @Resource
    private WeatherService weatherService;

    @Resource
    private PhraseService phraseService;

    /**
     * 测试发送模板消息
     */
    @Test
    public void testSendTemplateMsg() {
        wechatTemplateMsgService.sendTemplateMsg();
    }


    /**
     * 获取城市天气信息
     */
    @Test
    public void getCityWeather() {
        weatherService.getWeather("杭州");
    }


    /**
     * 获取城市天气信息
     */
    @Test
    public void getJuHeWeather() {
        WeatherInfo wea = weatherService.getJuHeWeather("杭州");
        System.out.println(wea);
    }



    /**
     * 获取每日一句
     */
    @Test
    public void getPhraseData() {
        String dailyPhrase = phraseService.getDailyPhrase();
        System.out.println(dailyPhrase);
    }
}
