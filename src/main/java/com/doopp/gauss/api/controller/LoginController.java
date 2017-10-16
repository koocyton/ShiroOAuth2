package com.doopp.gauss.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.LoginService;
import com.doopp.gauss.api.service.RestResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录界面
 *
 * Created by henry on 2017/7/11.
 */
@Controller
@RequestMapping(value = "api/v1/")
public class LoginController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    private LoginService loginService;

    private RestResponseService restResponseService;

    @Autowired
    public LoginController (LoginService loginService, RestResponseService restResponseService) {
        this.loginService = loginService;
        this.restResponseService = restResponseService;
    }
    /*
     * 提交登录
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public JSONObject login(HttpServletResponse response,
                            @RequestParam("account") String account,
                            @RequestParam("password") String password) {

        // 校验用户名，密码
        if (!loginService.checkAccountPassword(account, password)) {
            // 告诉客户端密码错误
            return restResponseService.error(response, 404, "Account or password is failed");
        }
        // 注册一个登录用户，生成 access token ，并缓存这个 key 对应的值 (account)
        String accessToken = loginService.createSessionToken(account);
        if (accessToken==null) {
            return restResponseService.error(response, 500, "can not login");
        }
        // 下发 access token
        return restResponseService.data(accessToken);
    }
}
