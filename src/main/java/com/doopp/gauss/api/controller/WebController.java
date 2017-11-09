package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WebController {

    @Autowired
    RoomSocketHandler roomSocketHandler;

    @Autowired
    private AccountService accountService;

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
    public String apiRoom(HttpServletRequest request, ModelMap modelMap) {
        String namePrefix = request.getParameter("namePrefix");
        if (namePrefix==null) {
            namePrefix = "kton";
        }
        int roomId = 1 + roomSocketHandler.getLastRoomId();
        modelMap.addAttribute("roomId", roomId);
        modelMap.addAttribute("namePrefix", namePrefix);
        return "api/room";
    }

    /*
     * 注册一个新号
     */
    @RequestMapping(value = "/register12", method = RequestMethod.POST)
    public String register(@RequestParam("namePrefix") String namePrefix, ModelMap modelMap) throws Exception {
        for(int ii=0; ii<=11; ii++) {
            String name = namePrefix + "_" + ii;
            UserEntity user = accountService.registerThenGetUser(name + "@gmail.com", "123456", name);
            accountService.registerSession(user);
        }
        modelMap.addAttribute("url", "/api-room?namePrefix=" + namePrefix);
        return "api/redirect";
    }
}
