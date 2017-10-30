package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.RoomSession;
import com.doopp.gauss.api.entity.dto.RoomDTO;
import com.doopp.gauss.api.utils.CommonUtils;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 进入或创建房间的接口
 *
 * Created by henry on 2017/10/20.
 */
@Controller
@RequestMapping(value = "/api/v1")
public class RoomController {

    @Autowired
    RoomSocketHandler roomSocketHandler;

    /*
     * 聊天 Demo 界面
     */
    @ResponseBody
    @RequestMapping(value = "/room/list")
    public List<RoomDTO> demoMain() {
        Map<Integer, RoomSession> roomMap = roomSocketHandler.getRooms();
        return new ArrayList<RoomDTO>() {{
            for(RoomSession room : roomMap.values()) {
                add(CommonUtils.modelMap(room, RoomDTO.class));
            }
        }};
    }
}
