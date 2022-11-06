package com.example.wechat.service.phrase;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.PhraseConstants;
import com.example.wechat.util.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * @author Administrator
 * @date 2022/11/6 12:07
 */
@Service
public class PhraseService {

    @Value("${juhe.phrase.key}")
    private String juHePhraseKey;

    /**
     * 获取每日一句
     *
     * @return
     */
    public String getDailyPhrase() {
        String requestUrl = MessageFormat.format(PhraseConstants.LANGUAGE_URL, juHePhraseKey);

        String responseStr = OkHttpUtil.getRequest(requestUrl);
        if (StringUtils.hasLength(responseStr)) {
            JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
            if (!responseObj.isEmpty()) {
                Integer errorCode = responseObj.getInteger("error_code");
                if (errorCode == 0) {
                    JSONObject resultObj = responseObj.getJSONObject("result");
                    if (!resultObj.isEmpty()) {
                        return resultObj.getString("text");
                    }
                }
            }
        }
        return null;
    }
}
