package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
import com.example.wechat.entity.WeatherInfo;
import com.example.wechat.service.phrase.PhraseService;
import com.example.wechat.service.weather.WeatherService;
import com.example.wechat.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

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

            String responseStr = OkHttpUtil.postRequest(requestUrl, requestData);
        }
    }

    /**
     * 发送模板消息
     */
    public void sendTemplateMsg() {
        String accessToken = wechatAccessTokenService.getAccessTokenFromWechat();
        if (StringUtils.hasLength(accessToken)) {
            String requestUrl = MessageFormat.format(WechatConstants.SEND_TEMPLATE_MSG_URL, accessToken);
            List<String> openIdList = wechatUserService.getUserList(accessToken);
            if (CollectionUtils.isEmpty(openIdList)) {
                logger.info("未查找到用户的openId");
                return;
            }

            WeatherInfo weather = weatherService.getJuHeWeather("杭州");

            ExecutorService executor = Executors.newFixedThreadPool(10, r -> new Thread(r, "发送模板消息"));
            try {
                List<Integer> resultList = openIdList.stream()
                        .map(openId -> CompletableFuture.supplyAsync(() -> {
                            JSONObject requestData = pkgRequestData(openId, weather);
                            logger.info("请求参数：{}", requestData.toString());
                            String responseStr = OkHttpUtil.postRequest(requestUrl, requestData);
                            if (StringUtils.hasLength(responseStr)) {
                                JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
                                Integer errCode = responseObj.getInteger("errcode");
                                if (errCode == 0) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                            return 0;
                        }, executor)).collect(Collectors.toList())
                        .stream().map(CompletableFuture::join).collect(Collectors.toList());

                Map<Integer, Long> resultMap = resultList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                Long successNum = resultMap.getOrDefault(1, 0L);
                Long failedNum = resultMap.getOrDefault(0, 0L);
                int totalNum = resultList.size();

                logger.info("模板发送的成功率为：{}，失败率为：{}",
                        (float) successNum / totalNum, (float) failedNum / totalNum);
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
        requestData.put("template_id", "bra5-dkrO41JVaVGiUiQZ_K_V6m3eySKWL53OAjnWuQ");

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
