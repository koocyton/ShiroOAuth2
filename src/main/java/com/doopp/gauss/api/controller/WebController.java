package com.doopp.gauss.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    /*
     * API 说明
     */
    @RequestMapping(value = "/api")
    public String demoMain() {
        return "api/help";
    }
}
