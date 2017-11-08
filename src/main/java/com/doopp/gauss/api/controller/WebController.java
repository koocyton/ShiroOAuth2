package com.doopp.gauss.api.controller;

import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @Autowired
    RoomSocketHandler roomSocketHandler;

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
    public String apiRoom(ModelMap modelMap) {
        int roomId = 1 + roomSocketHandler.getLastRoomId();
        String namePrefix = this.getRandomString();
        modelMap.addAttribute("roomId", roomId);
        modelMap.addAttribute("namePrefix", namePrefix);
        return "api/room";
    }

    private String getRandomString(){
        String str = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer sbf = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < 10; i++) {
            sbf.append(str.charAt((int) Math.round(Math.random() * (len-1))));
        }
        return sbf.toString();
    }
}
