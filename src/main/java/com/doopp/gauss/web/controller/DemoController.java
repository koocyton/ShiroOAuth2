package com.doopp.gauss.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * 测试的 Demo 工具
 * Created by Henry on 2017/8/23.
 */
@Controller
public class DemoController {

    @RequestMapping(value = "web-socket")
    public String socket() {
        return "web/web-socket";
    }

    @RequestMapping(value = "oauth/login")
    public String oauthLogin() {
        return "web/oauth-login";
    }
}
