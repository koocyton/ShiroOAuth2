package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.entity.dto.UserDTO;
import com.doopp.gauss.api.utils.CommonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户的 Api Controller
 *
 * Created by henry on 2017/10/14.
 */
@Controller
@RequestMapping(value = "api/v1/")
public class UserController {

    /**
     * 查询当前用户信息
     *
     * @param currentUser 当前用户
     * @return 返回用户信息
     */
    @ResponseBody
    @RequestMapping(value = "user/me", method = RequestMethod.GET)
    public UserDTO myInfo(@RequestAttribute("currentUser") UserEntity currentUser) {
        // GET user
        return CommonUtils.modelMap(currentUser, UserDTO.class);
    }
}
