package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
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
    public void actionDispatcher(WebSocketChannel socketChannel, String playerAction, JSONObject actionData) {
        // 必须是一个在线，并活着的玩家，才能操作
        Player player = playerService.getPlayer((Long) socketChannel.getAttribute("userId"));
        if (player!=null && player.isLiving()) {
            // 存在的房间
            Room room = roomService.getRoom(player.getRoom_id());
            if (room!=null) {
                // 转发 action
                switch (playerAction) {
                    // 用户准备
                    case Action.PLAYER_READY:
                        this.readyAction(room, player);
                        break;
                    // 狼选择杀人
                    case Action.WOLF_CHOICE:
                        this.wolfAction(room, player, actionData);
                        break;
                    // 先知选择查看
                    case Action.SEER_CHOICE:
                        this.seerAction(room, player, actionData);
                        break;
                    // 女巫选择杀人或救人
                    case Action.WITCH_CHOICE:
                        this.witchAction(room, player, actionData);
                        break;
                    // 猎人杀人
                    case Action.HUNTER_CHOICE:
                        this.hunterAction(room, player, actionData);
                        break;
                    // 玩家投票
                    case Action.PLAYER_VOTE:
                        this.playerVote(room, player, actionData);
                        break;
                    // 玩家聊天
                    case Action.PLAYER_SPEAK:
                        this.playerSpeak(room, player, actionData);
                        break;
                }
            }
        }
    }

    // 接受用户准备好了的消息
    private void readyAction(Room room, Player readyPlayer) {
        if (room.getAcceptAction().equals(Action.PLAYER_READY)) {
            playerService.iamReady(readyPlayer);
            this.sendMessage(room, Action.PLAYER_READY, room.getSeats());
            if (room.allReady()) {
                room.setAcceptAction(Action.REJECT_ACTION);
                room.setGameTask(new WerewolfGameTask(room));
                gameTaskExecutor.execute(room.getGameTask());
            }
        }
    }

    // 房间内公共频道说话
    private void playerSpeak(Room room, Player speakPlayer, JSONObject speakData) {
        speakData.put("speakPlayer", speakPlayer.getId());
        this.sendMessage(room, Action.PLAYER_SPEAK, speakData);
    }

    // 上行，狼人杀人
    private void wolfAction(Room room, Player actionPlayer, JSONObject actionData) {
        // 如果房间的状态为等待狼选择，并且，玩家身份是狼
        if (room.getAcceptAction().equals(Action.WOLF_CHOICE) && actionPlayer.getIdentity() == Identity.WOLF) {
            Long targetPlayerId = actionData.getLong("target-player");
            room.addCacheAction(new PlayerAction(Action.WOLF_CHOICE, actionPlayer.getId(), targetPlayerId));
            if (room.countCacheAction(Action.WOLF_CHOICE)==3) {
                room.gameContinue();
            }
        }
    }

    // 上行，预言家查身份
    private void seerAction(Room room, Player actionPlayer, JSONObject actionData) {
        if (room.getAcceptAction().equals(Action.WOLF_CHOICE) && actionPlayer.getIdentity()==Identity.SEER) {
            Long targetPlayerId = actionData.getLong("target-player");
            room.addCacheAction(new PlayerAction(Action.SEER_CHOICE, actionPlayer.getId(), targetPlayerId));
            actionData.put("identity", playerService.getPlayer(targetPlayerId).getIdentity());
            this.sendMessage(actionPlayer, Action.SEER_CHOICE, actionData);
        }
    }

    // 上行，女巫救人或毒杀
    private void witchAction(Room room, Player actionPlayer, JSONObject actionData) {
        if (room.getAcceptAction().equals(Action.WITCH_CHOICE) && actionPlayer.getIdentity()==Identity.WITCH) {
            Long targetPlayerId = actionData.getLong("target-player");
            room.addCacheAction(new PlayerAction(Action.WITCH_CHOICE, actionPlayer.getId(), targetPlayerId));
            room.gameContinue();
        }
    }

    // 上行，猎人杀人
    private void hunterAction(Room room, Player actionPlayer, JSONObject actionData) {
        if (room.getAcceptAction().equals(Action.HUNTER_CHOICE) && actionPlayer.getIdentity()==Identity.HUNTER) {
            Long targetPlayerId = actionData.getLong("target-player");
            room.addCacheAction(new PlayerAction(Action.HUNTER_CHOICE, actionPlayer.getId(), targetPlayerId));
            room.gameContinue();
        }
    }

    // 玩家投票
    private void playerVote(Room room, Player actionPlayer, JSONObject actionData) {
        if (room.getAcceptAction().equals(Action.PLAYER_VOTE)) {
            Long targetPlayerId = actionData.getLong("target-player");
            room.addCacheAction(new PlayerAction(Action.PLAYER_VOTE, actionPlayer.getId(), targetPlayerId));
            room.gameContinue();
        }
    }

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
            jsonObject.put("time", System.currentTimeMillis());
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
}
