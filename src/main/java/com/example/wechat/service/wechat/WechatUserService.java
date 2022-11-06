package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.WechatConstants;
import com.example.wechat.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户相关的service
 *
 * @author Administrator
 * @date 2022/11/5 22:22
 */
@Slf4j
@Service
public class WechatUserService {


    /**
     * 获取用户列表的openId
     *
     * @return 返回用户列表
     */
    public List<String> getUserList(String accessToken) {
        List<String> userList = new ArrayList<>();

        if (!StringUtils.hasLength(accessToken)) {
            return userList;
        }

        String requestUrl = MessageFormat.format(WechatConstants.USER_INFO_URL, accessToken);
        String responseStr = OkHttpUtil.getRequest(requestUrl);
        if (StringUtils.hasLength(responseStr)) {
            JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
            if (responseObj.isEmpty()) {
                log.error("返回的用户列表为空");
                return userList;
            }

            JSONObject data = responseObj.getJSONObject("data");
            if (data.isEmpty()) {
                return userList;
            }

            JSONArray jsonArray = data.getJSONArray("openid");
            if (!jsonArray.isEmpty()) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    userList.add(jsonArray.getString(i));
                }
            }
        }
        return userList;
    }


}
