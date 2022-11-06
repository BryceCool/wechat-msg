package com.example.wechat.service.wechat;

import com.example.wechat.util.EncodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Administrator
 * @date 2022/10/15 23:10
 */
@Service
public class WechatService {

    @Value("${wechat.token}")
    private String token;

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    /**
     * 开发者通过检验 signature 对请求进行校验（下面有校验方式）。
     * 若确认此次 GET 请求来自微信服务器，请原样返回 echostr 参数内容，则接入生效，成为开发者成功，
     * 否则接入失败。
     *
     * @return 验证成功，则返回echostr 参数，否则返回null
     */
    public String validateAccessWechat(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        if (validateWechatParam(token, timestamp, nonce, signature)) {
            System.out.println("微信接入成功");
            return echostr;
        }
        System.out.println("微信接入失败");
        return null;
    }

    /**
     * 加密/校验流程如下：
     * * 1）将token、timestamp、nonce三个参数进行字典序排序
     * * 2）将三个参数字符串拼接成一个字符串进行sha1加密
     * * 3）开发者获得加密后的字符串可与 signature 对比，标识该请求来源于微信
     *
     * @param token     测试参数设置的token
     * @param timestamp wechat 发送get 请求之后的返回参数
     * @param nonce     wechat 发送get 请求之后的返回参数
     * @param signature 验证信息
     * @return 返回true or false
     */
    private boolean validateWechatParam(String token, String timestamp, String nonce, String signature) {
        // 1. 字典序排序以下3 个参数
        String[] params = new String[]{token, timestamp, nonce};
        Arrays.sort(params);

        // 2. 将这三个参数拼接成一个字符串，并进行sha1 加密
        String paramStr = params[0] + params[1] + params[2];
        String encodeStr = EncodeUtil.sha1Param(paramStr);

        System.out.println(encodeStr);
        System.out.println(signature);
        // 3. 和signature 进行比对
        if (StringUtils.hasLength(encodeStr) && signature.equals(encodeStr)) {
            System.out.println("接入成功");
            return true;
        } else {
            System.out.println("接入失败");
            return false;
        }
    }
}
