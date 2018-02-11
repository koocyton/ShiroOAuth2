package com.doopp.gauss.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/helper")
public class RobotController {

    @RequestMapping(value = "/robot")
    public String robot(HttpServletRequest request, ModelMap modelMap) {
        String namePrefix = request.getParameter("namePrefix");
        if (namePrefix==null) {
            namePrefix = "kton";
        }
        modelMap.addAttribute("namePrefix", namePrefix);
        return "robot";
    }
}
