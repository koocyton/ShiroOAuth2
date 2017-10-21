package com.doopp.gauss.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/demo")
public class WebController {

    /*
     * web 测试
     */
    @RequestMapping(value = "/chat-room")
    public String chatRoom() {
        return "demo/chat_room";
    }

    /*
     * web 测试
     */
    @RequestMapping(value = "/chat-room/login")
    public String chatRoomLogin() {
        return "demo/chat_room_login";
    }

    /*
     * web 测试
     */
    @RequestMapping(value = "/chat-room/list")
    public String chatRoomList() {
        return "demo/chat_room_list";
    }
}
