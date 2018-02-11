package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import io.undertow.websockets.core.WebSocketChannel;

public interface RoomService {

    void playerJoin(Player player);

    void playerLeave(Player player);

    // 通过 房间 id 获取房间
    Room getRoom(int roomId);
}
