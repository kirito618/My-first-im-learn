package com.hzy.im.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: hzy
 * Date Time:      2021/5/24 17:27
 */

@Controller
public class IndexController {

    @RequestMapping(value = "/toIndex")
    public String toIndexPage() {
        return "index";
    }

    @RequestMapping(value = "/")
    public String toIndexPageDirectly() {
        return "index";
    }

    /**
     * 登录成功后跳转到聊天页面.
     */
    @GetMapping(value = "/toChatroom")
    public String toChatroom() {
        return "chat";
    }

}