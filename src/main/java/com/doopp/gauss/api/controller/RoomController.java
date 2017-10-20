package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.entity.dto.RoomDTO;
import com.doopp.gauss.api.service.RoomService;
import com.doopp.gauss.api.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 进入或创建房间的接口
 *
 * Created by henry on 2017/10/20.
 */
@Controller
@RequestMapping(value = "api/v1/")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 创建房间
     *
     * @param currentUser 当前用户
     * @param roomName 房间名
     * @return 房间信息
     * @throws Exception 如果不能创建房间
     */
    @ResponseBody
    @RequestMapping(value = "create-room", method = RequestMethod.GET)
    public RoomDTO createRoom(@RequestAttribute("currentUser") UserEntity currentUser, @RequestParam("roomName") String roomName) throws Exception {
        RoomEntity room = roomService.createRoom(currentUser, roomName);
        if (room==null) {
            throw new Exception("can not create room");
        }
        return CommonUtils.modelMap(room, RoomDTO.class);
    }

    /**
     * 加入到房间
     *
     * @param roomId 房间 ID
     * @param currentUser 当前用户
     * @return 加入的房间信息
     * @throws Exception 进入房间失败
     */
    @ResponseBody
    @RequestMapping(value = "join-room", method = RequestMethod.GET)
    public RoomDTO joinRoom(@RequestParam("room") int roomId, @RequestAttribute("currentUser") UserEntity currentUser) throws Exception {
        RoomEntity room = roomService.joinRoom(roomId, currentUser);
        if (room==null) {
            throw new Exception("can not create room");
        }
        return CommonUtils.modelMap(room, RoomDTO.class);
    }

    /**
     * 返回房间列表
     *
     * @return 房间列表
     */
    @ResponseBody
    @RequestMapping(value = "room-list", method = RequestMethod.GET)
    public Map<Integer, RoomDTO> roomList() {
        return null;
    }
}
