package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
import com.example.wechat.entity.WeatherInfo;
import com.example.wechat.service.weather.WeatherService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微信模板消息服务类
 *
 * @author Administrator
 * @date 2022/11/5 20:25
 */
@Service
public class WechatTemplateMsgService {

    public static final Logger logger = LoggerFactory.getLogger(WechatTemplateMsgService.class);

    @Resource
    private WechatAccessTokenService wechatAccessTokenService;

    @Resource
    private WechatUserService wechatUserService;

    @Resource
    private WeatherService weatherService;

    @Resource
    private PhraseService phraseService;

    /**
     * 设置行业
     */
    public void setIndustry() {
        String accessToken = wechatAccessTokenService.getAccessTokenFromWechat();
        if (StringUtils.hasLength(accessToken)) {
            String requestUrl = MessageFormat.format(WechatConstants.SET_INDUSTRY_URL, accessToken);

            JSONObject requestData = new JSONObject();
            requestData.put("industry_id1", "1");
            requestData.put("industry_id2", "4");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(RequestBody.create(requestData.toString().getBytes())).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseStr = Objects.requireNonNull(response.body()).string();
                    if (StringUtils.hasLength(responseStr)) {
                        System.out.println(responseStr);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("设置行业失败");
            }
        }
    }

    /**
     * 发送模板消息
     */
    public void sendTemplateMsg() {
        String accessToken = wechatAccessTokenService.getAccessTokenFromWechat();
        if (StringUtils.hasLength(accessToken)) {
            String requestUrl = MessageFormat.format(WechatConstants.SEND_TEMPLATE_MSG_URL, accessToken);

            ExecutorService executor = Executors.newFixedThreadPool(10, r -> new Thread(r, "发送模板消息"));

            List<String> openIdList = wechatUserService.getUserList(accessToken);
            WeatherInfo weather = weatherService.getJuHeWeather("杭州");

            try {
                openIdList.forEach(openId -> {
                    CompletableFuture.runAsync(() -> {
                        JSONObject requestData = pkgRequestData(openId, weather);
                        logger.info("请求参数：{}", requestData.toString());

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(requestUrl)
                                .post(RequestBody.create(JSON.toJSONString(requestData).getBytes())).build();

                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                String responseStr = Objects.requireNonNull(response.body()).string();
                                if (StringUtils.hasLength(responseStr)) {
                                    logger.info("返回结果：" + responseStr);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("设置行业失败");
                        }
                    }, executor);
                });
            } finally {
                executor.shutdown();
            }
        }
    }

    /**
     * 组装请求参数
     *
     * @return 返回请求参数
     */
    public JSONObject pkgRequestData(String openId, WeatherInfo weatherInfo) {
        JSONObject requestData = new JSONObject();
        requestData.put("touser", openId);
        requestData.put("template_id", "4_GbZvLpg-7BJLyw081-y_YI_ywBDgByOZSNxMQeHvY");

        JSONObject data = new JSONObject();
        // first data
        JSONObject firstObj = new JSONObject();
        firstObj.put("value", "Hello, 老铁, 今日份关心已送达！");
        firstObj.put("color", "#173177");
        data.put("first", firstObj);

        // second data
        JSONObject secondObj = new JSONObject();
        secondObj.put("value", weatherInfo.getCity() + " " + weatherInfo.getDate());
        secondObj.put("color", "#173177");
        data.put("second", secondObj);

        // weather data
        JSONObject weatherObj = new JSONObject();
        weatherObj.put("value", weatherInfo.getWeather());
        weatherObj.put("color", "#173177");
        data.put("weather", weatherObj);

        // high weather data
        JSONObject highObj = new JSONObject();
        highObj.put("value", weatherInfo.getHighTemperature());
        highObj.put("color", "#173177");
        data.put("high", highObj);

        // low weather data
        JSONObject lowObj = new JSONObject();
        lowObj.put("value", weatherInfo.getLowTemperature());
        lowObj.put("color", "#173177");
        data.put("low", lowObj);

        // dress advice data
        JSONObject dressAdviceObj = new JSONObject();
        dressAdviceObj.put("value", weatherInfo.getDressAdvice());
        dressAdviceObj.put("color", "#173177");
        data.put("dress", dressAdviceObj);

        // remark data
        JSONObject remarkObj = new JSONObject();
        remarkObj.put("value", "God Bless You！");
        remarkObj.put("color", "#173177");
        data.put("remark", remarkObj);

        requestData.put("data", data);
        return requestData;
    }

}
