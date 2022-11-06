package com.example.wechat.constant;

/**
 * @author Administrator
 * @date 2022/11/5 20:46
 */
public class WechatConstants {

    /**
     * 获取access_token
     * 1. grant_type    必填      获取access_token填写client_credential
     * 2. appid         必填      第三方用户唯一凭证
     * 3. secret        必填      第三方用户唯一凭证密钥，即appsecret
     */
    public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";


    /**
     * 设置所属行业
     * 1. access_token      是       接口调用凭证
     * 2. industry_id1      是       公众号模板消息所属行业编号
     * 3. industry_id2      是       公众号模板消息所属行业编号
     */
    public static final String SET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token={0}";


    /**
     * 发送模板消息
     * 1. touser           是            接收者openId
     * 2. template_id      是            模板id
     * 3. data             是            模板数据
     */
    public static final String SEND_TEMPLATE_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={0}";


    /**
     * 获取用户列表
     * 1. access_token      是       调用接口凭证
     */
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token={0}";

}
