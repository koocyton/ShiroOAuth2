package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.GameService;
import com.doopp.gauss.common.service.PlayerService;
import com.doopp.gauss.common.service.RoomService;
import com.doopp.gauss.common.service.SocketChannelService;
import com.doopp.gauss.common.task.WerewolfGameTask;
import io.undertow.websockets.core.WebSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("gameService")
public class GameServiceImpl implements GameService {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Autowired
    private PlayerService playerService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SocketChannelService socketChannelService;

    @Autowired
    private ThreadPoolTaskExecutor gameTaskExecutor;

    @Override
    public void actionDispatcher(WebSocketChannel socketChannel, String playerAction, Object actionData) {
        Player player = playerService.getPlayer((Long) socketChannel.getAttribute("userId"));
        Room room = roomService.getRoom(player.getRoom_id());
        // logger.info(" >> " + room.getId() +  " / " + player.getId() + " : " + playerAction + " : " + actionData);

        // 校验房间正在等待的状态
        if (room.getAcceptAction().equals(playerAction)) {
            // 转发 action
            switch (playerAction) {
                // 用户准备
                case Action.PLAYER_READY:
                    this.readyAction(room, player);
                    break;
                // 狼选择杀人
                case Action.WOLF_CHOICE:
                    //this.werewolfAction(room, player, messageObject);
                    break;
                // 先知选择查看
                case Action.SEER_CHOICE:
                    //this.seerAction(room, player, messageObject);
                    break;
                // 女巫选择杀人或救人
                case Action.WITCH_CHOICE:
                    //this.witchAction(room, player, messageObject);
                    break;
                // 猎人杀人
                case Action.HUNTER_CHOICE:
                    //this.hunterAction(room, player, messageObject);
                    break;
                // 玩家说话
                case Action.PLAYER_SPEAK:
                    // this.playerSpeak(room, player, messageObject);
                    break;
                // 玩家投票
                case Action.PLAYER_VOTE:
                    //this.playerVote(room, player, messageObject);
                    break;
            }
        }
    }

    // 接受用户准备好了的消息
    @Override
    public void readyAction(Room room, Player readyPlayer) {
        playerService.iamReady(readyPlayer);
        if (room.allReady()) {
            gameTaskExecutor.execute(new WerewolfGameTask(room));
        }
    }

//    // 上行，狼人杀人
//    @Override
//    public void werewolfAction(Room room, Player actionPlayer, JSONObject messageObject) {
//        if (actionPlayer.getIdentity()==Identity.WOLF) {
//            Long playerId = messageObject.getLong("choice-target");
//            Player targetPlayer = playerDao.getPlayerById(playerId);
//            room.setCacheAction(new PlayerAction(Action.WOLF_CHOICE, actionPlayer, targetPlayer));
//            // 如果三个狼人都提交了，就 notify game task continue
//            if (room.cacheActionCount(Action.WOLF_CHOICE)==3) {
//                room.getGameTask().notify();
//            }
//        }
//    }
//
//    // 上行，预言家查身份
//    @Override
//    public void seerAction(Room room, Player actionPlayer, JSONObject messageObject) {
//        if (actionPlayer.getIdentity()==Identity.SEER) {
//            Long playerId = messageObject.getLong("choice-target");
//            Player targetPlayer = playerDao.getPlayerById(playerId);
//            roomDao.cacheAction(Action.SEER_CHOICE, actionPlayer, targetPlayer);
//            this.sendMessage(actionPlayer, targetPlayer.getIdentity().toString());
//        }
//    }
//
//    // 上行，女巫救人或毒杀
//    @Override
//    public void witchAction(Room room, Player actionPlayer, JSONObject messageObject) {
//        if (actionPlayer.getIdentity()==Identity.WITCH) {
//            Long playerId = messageObject.getLong("choice-target");
//            Player targetPlayer = playerDao.getPlayerById(playerId);
//            roomDao.cacheAction(Action.WITCH_CHOICE, actionPlayer, targetPlayer);
//            // 如果女巫提交完毕
//            if (room.cacheActionCount(Action.WITCH_CHOICE)==1) {
//                room.getGameTask().notify();
//            }
//        }
//    }
//
//    // 上行，猎人杀人
//    @Override
//    public void hunterAction(Room room, Player actionPlayer, JSONObject messageObject) {
//        if (actionPlayer.getIdentity()==Identity.HUNTER) {
//            Long playerId = messageObject.getLong("choice-target");
//            Player targetPlayer = playerDao.getPlayerById(playerId);
//            roomDao.cacheAction(Action.HUNTER_CHOICE, actionPlayer, targetPlayer);
//            // 如果猎人提交完毕
//            if (room.cacheActionCount(Action.HUNTER_CHOICE)==1) {
//                room.getGameTask().notify();
//            }
//        }
//    }
//
//    // 玩家投票
//    @Override
//    public void playerVote(Room room, Player actionPlayer, JSONObject messageObject) {
//        if (actionPlayer.isLiving()) {
//            Long playerId = messageObject.getLong("choice-target");
//            Player targetPlayer = playerDao.getPlayerById(playerId);
//            roomDao.cacheAction(Action.PLAYER_VOTE, actionPlayer, targetPlayer);
//        }
//    }

    // ************** 发送信息到某个用户
    @Override
    public void sendMessage(Player player, String message) {
        if (player!=null && message!=null && player.isLiving()) {
            socketChannelService.sendMessage(player.getId(), message);
        }
    }
    @Override
    public void sendMessage(Player player, String action, Object data) {
        if (player!=null && player.isLiving() && action!=null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", action);
            jsonObject.put("data", data);
            this.sendMessage(player, jsonObject.toJSONString());
        }
    }
    // ************** 发送信息到多个用户
    @Override
    public void sendMessage(Player[] players, String message) {
        for(Player player : players) {
            this.sendMessage(player, message);
        }
    }
    @Override
    public void sendMessage(Player[] players, String action, Object data) {
        for(Player player : players) {
            this.sendMessage(player, action, data);
        }
    }
    // ************** 发送信息到房间
    @Override
    public void sendMessage(Room room, String message) {
        if (room!=null) {
            this.sendMessage(room.getSeats(), message);
        }
    }
    @Override
    public void sendMessage(Room room, String action, Object data) {
        if (room!=null) {
            this.sendMessage(room.getSeats(), action, data);
        }
    }

//    // 房间内公共频道说话
//    private void actionSpeak(WebSocketSession socketSession, JSONObject messageObject) throws IOException {
//        WebSocketSession[] inRoomSockets = this.getSocketsInRoom(socketSession);
//        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
//        for (WebSocketSession userSocketSession : inRoomSockets) {
//            if (!userSocketSession.equals(socketSession)) {
//                messageObject.put("sender", player.getNickname());
//                TextMessage message = new TextMessage(messageObject.toJSONString());
//                userSocketSession.sendMessage(message);
//            }
//        }
//    }
//
//    // 玩游戏
//    private void actionPlay(WebSocketSession socketSession, JSONObject messageObject, String action) {
//        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
//        Room room = roomDao.getRoomById(player.getRoom_id());
//        this.actionDispatcher(room, player, action, messageObject);
//    }
//
//    // 取出当前用户所在房间的 sockets
//    private WebSocketSession[] getSocketsInRoom(WebSocketSession socketSession) {
//        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
//        Room room = roomDao.getRoomById(player.getRoom_id());
//        Player[] players = room.getPlayers();
//        WebSocketSession[] inRoomSockets = {};
//        int ii = inRoomSockets.length;
//        for (Player otherPlayer : players) {
//            if (otherPlayer!=null) {
//                inRoomSockets[ii] = socketGroup.get(otherPlayer.getId());
//                ii++;
//            }
//        }
//        return inRoomSockets;
//    }
//
//    public WebSocketSession getWebsocketById(Long userId) {
//        return socketGroup.get(userId);
//    }


//    // 用户建立连接
//    private void playerJoin(WebSocketSession socketSession) {
//        // 索引
//        Long userId = (Long) socketSession.getAttributes().get("userId");
//        // 将用户保存在用户组
//        this.playerJoin(userId);
//    }
//
//    // 用户断开连接
//    private void playerLeave(WebSocketSession socketSession) {
//        // 索引
//        Long userId = (Long) socketSession.getAttributes().get("userId");
//        // 从用户组里移除
//        this.playerLeave(userId);
//    }
//
//    // 用户建立连接
//    private void playerJoin(Long userId) {
//        // 将用户保存在用户组
//        User user = accountService.getCacheUser(userId);
//        Player player = UserMapper.INSTANCE.userToPlayer(user);
//        playerGroup.put(userId, player);
//    }
//
//    // 用户断开连接
//    private void playerLeave(Long userId) {
//        // 从用户组里移除
//        playerGroup.remove(userId);
//    }
//
//    // 用户进入房间
//    private void joinRoom(Player player) {
//
//    }
//
//    // 用户离开房间
//    private void leaveRoom(Player player) {
//        int roomId = player.getRoom_id();
//        Room room = roomGroup.get(roomId);
//        room.playerLeave(player);
//    }
//
//    // socket 建立连接
//    private void socketConnect(WebSocketSession socketSession) {
//        if (socketSession!=null) {
//            // 索引
//            Long userId = (Long) socketSession.getAttributes().get("userId");
//            // 先断开
//            this.socketDisconnect(socketGroup.get(userId));
//            // 将连接保存到 socket group 里
//            socketGroup.put(userId, socketSession);
//        }
//    }
//
//    // socket 断开连接
//    private void socketDisconnect(WebSocketSession socketSession) {
//        if (socketSession!=null) {
//            Long userId = (Long) socketSession.getAttributes().get("userId");
//            if (socketSession.isOpen()) {
//                try {
//                    socketSession.close();
//                } catch (IOException e) {
//                    logger.warn("WARN : User {} socketSession IOException on closing", userId);
//                }
//            }
//            // 从socket组里移除
//            socketGroup.remove(userId);
//        }
//    }
}
