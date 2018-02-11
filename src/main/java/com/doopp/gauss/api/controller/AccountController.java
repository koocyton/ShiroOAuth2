package com.doopp.gauss.api.controller;

import com.doopp.gauss.common.entity.SessionUser;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.mapper.UserMapper;
import com.doopp.gauss.common.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api")
public class AccountController {


    @Autowired
    private AccountService accountService;

    /**
     * 平台登陆
     *
     * @param data 平台数据
     * @param platform 平台类型
     * @return EncryptUserDTO
     * @throws Exception 账号或密码错误
     */
    @ResponseBody
    @RequestMapping(value = "/platform-login", method = RequestMethod.POST)
    public SessionUser platformLogin(@RequestParam("data") String data,
                                     @RequestParam("platform") String platform) throws Exception {
        User user = accountService.getUserOnPlatformLogin(platform, data);
        SessionUser sessionUser = UserMapper.INSTANCE.userToSessionUser(user);
        accountService.setCacheUser(user, sessionUser.getSession_token());
        return sessionUser;
    }

    /**
     * 用户登录
     * @param account 用户的账号
     * @param password 用户的密码
     * @return accessToken
     * @throws Exception 账号或密码错误
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public SessionUser login(@RequestParam("account") String account,
                             @RequestParam("password") String password) throws Exception {
        User user = accountService.getUserOnAccountLogin(account, password);
        SessionUser sessionUser = UserMapper.INSTANCE.userToSessionUser(user);
        accountService.setCacheUser(user, sessionUser.getSession_token());
        return sessionUser;
    }

    /**
     * 用户注册
     * @param account 用户的账号
     * @param password 用户的密码
     * @return accessToken
     * @throws Exception 账号或密码错误
     */
    @ResponseBody
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public SessionUser register(@RequestParam("account") String account,
                             @RequestParam("password") String password) throws Exception {
        User user = accountService.getUserOnAccountRegister(account, password);
        SessionUser sessionUser = UserMapper.INSTANCE.userToSessionUser(user);
        accountService.setCacheUser(user, sessionUser.getSession_token());
        return sessionUser;
    }
}
