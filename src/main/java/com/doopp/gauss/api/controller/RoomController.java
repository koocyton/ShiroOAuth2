package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.dto.RoomDTO;
import com.doopp.gauss.api.utils.CommonUtils;
import com.doopp.gauss.server.websocket.LiveSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractList;
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
    LiveSocketHandler liveSocketHandler;

    /*
     * 聊天 Demo 界面
     */
    @ResponseBody
    @RequestMapping(value = "/room/list")
    public List<RoomDTO> demoMain() {
        Map<Integer, RoomEntity> roomMap = liveSocketHandler.getRoomList();
        return new ArrayList<RoomDTO>() {{
            for(RoomEntity room : roomMap.values()) {
                add(CommonUtils.modelMap(room, RoomDTO.class));
            }
        }};
    }
}
