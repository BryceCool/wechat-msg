package com.example.wechat.controller;

import com.example.wechat.service.wechat.WechatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信消息controller
 *
 * @author Administrator
 * @date 2022/10/15 21:57
 */

@RestController
@RequestMapping("/valid")
public class WechatMsgController {

    @Resource
    private WechatService wechatService;

    /**
     * 验证微信消息
     */
    @GetMapping("/msg")
    public String getValidateWechatMsg(HttpServletRequest request) {
        return wechatService.validateAccessWechat(request);
    }

}
