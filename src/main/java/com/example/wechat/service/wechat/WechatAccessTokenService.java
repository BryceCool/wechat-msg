package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
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
 * 获取微信access_token 的服务类
 *
 * @author Administrator
 * @date 2022/11/5 20:27
 */
@Service
public class WechatAccessTokenService {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    /**
     * 从微信获取access_token
     */
    public String getAccessTokenFromWechat() {
        String requestUrl = MessageFormat.format(WechatConstants.ACCESS_TOKEN_URL, appId, appSecret);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseStr = Objects.requireNonNull(response.body()).string();
                if (StringUtils.hasLength(responseStr)) {
                    JSONObject parse = (JSONObject) JSONObject.parse(responseStr);
                    return parse.getString("access_token");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("获取Access Token 失败");
        }
        return null;
    }

}
