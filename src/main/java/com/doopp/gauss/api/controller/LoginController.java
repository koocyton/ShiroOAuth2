package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.entity.dto.SessionKeyDTO;
import com.doopp.gauss.api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 登录界面
 *
 * Created by henry on 2017/10/19.
 */
@Controller
@RequestMapping(value = "api/v1/")
public class LoginController {

    @Autowired
    private AccountService accountService;

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public SessionKeyDTO login(@RequestParam("account") String account, @RequestParam("password") String password) throws Exception {
        // GET user
        UserEntity user = accountService.getUserOnLogin(account, password);
        String accessToken = accountService.registerSession(user);
        return new SessionKeyDTO(accessToken);
    }
}
