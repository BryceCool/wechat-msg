package com.example.wechat.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.constant.PhraseConstants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseStr = Objects.requireNonNull(response.body()).string();

                JSONObject responseObj = (JSONObject) JSONObject.parse(responseStr);
                Integer errorCode = responseObj.getInteger("error_code");
                if (errorCode == 0) {
                    JSONObject resultObj = responseObj.getJSONObject("result");
                    if (!resultObj.isEmpty()) {
                        return resultObj.getString("text");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("获取每日一句失败");
        }
        return null;
    }
}
