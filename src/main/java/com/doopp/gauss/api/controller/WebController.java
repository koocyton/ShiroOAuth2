package com.doopp.gauss.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    /*
     * 聊天 Demo 界面
     */
    @RequestMapping(value = "/demo/main")
    public String demoMain() {
        return "demo/main";
    }

    /*
     * 登录界面
     */
    @RequestMapping(value = "/demo/login")
    public String login() {
        return "demo/login";
    }

    /*
     * 注册用户界面
     */
    @RequestMapping(value = "/demo/register")
    public String register() {
        return "demo/register";
    }

    /*
     * 房间列表
     */
    @RequestMapping(value = "/demo/hall")
    public String roomList() {
        return "demo/hall";
    }

    /*
     * 创建房间
     */
    @RequestMapping(value = "/demo/create-room")
    public String chatRoom() {
        return "demo/create_room";
    }

    /*
     * 进入房间
     */
    @RequestMapping(value = "/demo/room/{roomId}")
    public String joinRoom(@PathVariable("roomId") int ruroomIdle) {
        return "demo/room";
    }

    /*
     * API 列表
     */
    @RequestMapping(value = "/demo/api-doc")
    public String showApi() {
        return "demo/api_doc";
    }

    /*
     * API 列表
     */
    @RequestMapping(value = "/help/api")
    public String apiPortal() {
        return "help/portal";
    }
}
