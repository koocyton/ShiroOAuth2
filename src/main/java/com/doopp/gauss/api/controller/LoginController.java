package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.entity.dto.AccessTokenDTO;
import com.doopp.gauss.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 登录界面
 *
 * Created by henry on 2017/7/11.
 */
@Controller
@RequestMapping(value = "api/v1/")
public class LoginController {

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    /*
     * 提交登录
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public AccessTokenDTO login(@RequestParam("account") String account, @RequestParam("password") String password) throws Exception {

        // 校验用户名，密码
        UserEntity user = userService.getByAccentPassword(account, password);
        if (user==null) {
            throw new Exception("server can not login");
        }
        // 注册一个登录用户，生成 access token ，并缓存这个 key 对应的值 (account)
        String accessToken = loginService.createSessionToken(user);
        // 下发 access token
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setAccessToken(accessToken);
        return accessTokenDTO;
    }
}
