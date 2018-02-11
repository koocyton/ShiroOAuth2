package com.doopp.gauss.api.controller;

import com.doopp.gauss.common.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户的 Api Controller
 *
 * Created by henry on 2017/10/14.
 */
@Controller
@RequestMapping(value = "api/")
public class UserController {

    @ResponseBody
    @RequestMapping(value = "/user/me", method = RequestMethod.GET)
    public User getUserInfo(@RequestAttribute("sessionUser") User sessionUser) {
        return sessionUser;
    }
}
