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
    public String apiHelp() {
        return "api/help";
    }

    /*
     * API 文档
     */
    @RequestMapping(value = "/api-doc")
    public String apiDoc() {
        return "api/doc";
    }

    /*
     * 长连接 ，房间示例
     */
    @RequestMapping(value = "/api-room")
    public String apiRoom() {
        return "api/room";
    }
}
