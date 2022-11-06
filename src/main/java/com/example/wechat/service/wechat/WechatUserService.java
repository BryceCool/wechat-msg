package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户相关的service
 *
 * @author Administrator
 * @date 2022/11/5 22:22
 */
@Slf4j
@Service
public class WechatUserService {

    @Resource
    private WechatAccessTokenService wechatAccessTokenService;

    /**
     * 获取用户列表的openId
     *
     * @return 返回用户列表
     */
    public List<String> getUserList(String accessToken) {
        List<String> userList = new ArrayList<>();
        if (StringUtils.hasLength(accessToken)) {
            String requestUrl = MessageFormat.format(WechatConstants.USER_INFO_URL, accessToken);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(requestUrl).get().build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseStr = Objects.requireNonNull(response.body()).string();
                    if (StringUtils.hasLength(responseStr)) {
                        JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
                        if (responseObj.isEmpty()) {
                            log.error("返回的用户列表为空");
                            return userList;
                        }

                        JSONObject data = responseObj.getJSONObject("data");
                        if (!data.isEmpty()) {
                            JSONArray jsonArray = data.getJSONArray("openid");
                            for (int i = 0; i < jsonArray.size(); i++) {
                                userList.add(jsonArray.getString(i));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("获取Access Token 失败");
            }
        }
        return userList;
    }


}
