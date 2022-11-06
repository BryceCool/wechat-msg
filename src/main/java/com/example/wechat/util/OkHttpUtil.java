package com.example.wechat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Administrator
 * @date 2022/11/6 17:26
 */
public class OkHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    /**
     * OkHttp 发起get 请求
     *
     * @param requestUrl 请求的url
     * @return 返回请求结果
     */
    public static String getRequest(String requestUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                ResponseBody body = response.body();
                if (!Objects.isNull(body)) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            logger.error("发起Get 请求：{} , 失败", requestUrl);
            throw new RuntimeException("发起Get 请求失败", e);
        }
        return null;
    }

    /**
     * OkHttp 发起Post 请求
     *
     * @param requestUrl 请求的url
     * @return 返回请求结果
     */
    public static String postRequest(String requestUrl, JSONObject requestData) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(RequestBody.create(JSON.toJSONString(requestData).getBytes()))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                ResponseBody body = response.body();
                if (!Objects.isNull(body)) {
                    String responseStr = body.string();
                    if (StringUtils.hasLength(responseStr)) {
                        logger.info("请求：{}, 参数：{} 的返回结果：{}",
                                requestUrl, JSON.toJSONString(requestData), responseStr);
                        return responseStr;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("发起Post 请求：{} , 失败", requestUrl);
            throw new RuntimeException("发起Post 请求失败：", e);
        }
        return null;
    }
}
