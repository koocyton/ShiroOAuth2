package com.doopp.gauss.common.entity;

import com.doopp.gauss.server.task.GameTask;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
@Data
public class Room {

    // 房间 ID
    int id;

    // 房间的用户
    User[] users = {
        null, null, null, null, null, null,
        null, null, null, null, null, null,
    };
}
