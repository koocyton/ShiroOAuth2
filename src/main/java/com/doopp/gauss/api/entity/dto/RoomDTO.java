package com.doopp.gauss.api.entity.dto;

import com.doopp.gauss.api.entity.RoomEntity;
import lombok.Data;

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

    // 房间名
    private String name;

    // 房主
    private UserDTO owner;

    // 围观玩家
    private Map<Long, UserDTO> watchUsers = new HashMap<>();

    // 游戏玩家
    private Map<Long, Long> gameUsersId = new HashMap<>();

    // 游戏状态
    private RoomEntity.GameStatus gameStatus = RoomEntity.GameStatus.Resting;

    // 游戏名
    private int gameType;
}
