package com.doopp.gauss.api.entity.dto;

import com.doopp.gauss.api.entity.UserEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/8/26.
 */
@Data
public class RoomDTO {

    // 房间 ID
    private int id;

    // 房间有多少个座位，即最多坐多少人
    // private int seatCount;

    // 房间名
    private String name;

    // 房主
    private UserDTO owner;

    // 前排玩家
    private Map<Long, UserDTO> frontUsers = new HashMap<>();

    // 围观玩家
    private Map<Long, UserDTO> watchUsers = new HashMap<>();
}
