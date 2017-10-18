package com.doopp.gauss.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DemoController {

    @RequestMapping(value = "/chat-room")
    public String chatRoom(HttpServletRequest request) {
        return "demo/old_chat_room";
    }
}
