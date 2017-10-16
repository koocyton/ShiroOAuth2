package com.doopp.gauss.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebChatController {

    // private static final Logger logger = LoggerFactory.getLogger(WebChatController.class);

    @RequestMapping(value = "/chat-room")
    public String chatRoom(HttpServletRequest request) {
        return "socket/demo/chat_room";
        // return CommonUtil.isMobileClient(request) ? "socket/demo/mobile_chat_room" : "socket/demo/chat_room";
    }
}
