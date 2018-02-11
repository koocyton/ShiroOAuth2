package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.defined.PlayerStatus;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.mapper.UserMapper;
import com.doopp.gauss.common.service.AccountService;
import com.doopp.gauss.common.service.PlayerService;
import com.doopp.gauss.common.service.RoomService;
import com.doopp.gauss.common.service.SocketChannelService;
import io.undertow.websockets.core.WebSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private static Map<Long, Player> playerGroup = new HashMap<>();

    @Autowired
    private SocketChannelService socketChannelService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoomService roomService;

    // 用户建立连接
    @Override
    public void join(WebSocketChannel socketChannel) {
        Long uid = socketChannelService.getUidByChannel(socketChannel);
        this.join(uid);
    }

    // 用户建立连接
    @Override
    public void join(Long uid) {
        this.leave(uid);
        User user = accountService.getCacheUser(uid);
        if (user!=null) {
            Player player = UserMapper.INSTANCE.userToPlayer(user);
            playerGroup.put(uid, player);
            roomService.playerJoin(player);
        }
    }

    // 用户断开连接
    @Override
    public void leave(WebSocketChannel socketChannel) {
        Long uid = socketChannelService.getUidByChannel(socketChannel);
        this.leave(uid);
    }

    // 用户断开连接
    @Override
    public void leave(Long uid) {
        Player player = playerGroup.get(uid);
        if (player!=null) {
            playerGroup.remove(uid);
            roomService.playerLeave(player);
        }
    }

    // 获取用户
    @Override
    public Player getPlayer(WebSocketChannel socketChannel) {
        Long uid = socketChannelService.getUidByChannel(socketChannel);
        return this.getPlayer(uid);
    }

    // 获取用户
    @Override
    public Player getPlayer(Long uid) {
        return playerGroup.get(uid);
    }

    @Override
    public void iamReady(Player player) {
        if (player!=null) {
            Room room = roomService.getRoom(player.getRoom_id());
            if (room != null) {
                player.setStatus(PlayerStatus.READY);
            }
        }
    }
}
