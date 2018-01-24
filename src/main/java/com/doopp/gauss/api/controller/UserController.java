package com.doopp.gauss.api.controller;

import com.doopp.gauss.common.dto.UserDTO;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.mapper.UserMapper;
import com.doopp.gauss.common.task.WerewolfGameTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    public UserDTO getUserInfo(@RequestAttribute("sessionUser") User sessionUser) {
        return UserMapper.INSTANCE.userToUserDTO(sessionUser);
    }
}
