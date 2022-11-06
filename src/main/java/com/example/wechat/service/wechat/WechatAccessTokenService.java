package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
import com.example.wechat.util.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

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

        String responseStr = OkHttpUtil.getRequest(requestUrl);
        if (StringUtils.hasLength(responseStr)) {
            JSONObject parse = (JSONObject) JSONObject.parse(responseStr);
            return parse.getString("access_token");
        }
        return null;
    }

}
