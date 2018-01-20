package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.task.WerewolfGameTask;
import com.doopp.gauss.server.websocket.GameSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service("playService")
public class PlayServiceImpl implements PlayService {

    private static String WOF_ID = "wolf";
    private static String VLG_ID = "villager";
    private static String WIH_ID = "witch";
    private static String HNT_ID = "hunter";
    private static String SER_ID = "seer";
    private static String CPT_ID = "cupid";

    private final String[] identities = new String[] {
        WOF_ID, WOF_ID, WOF_ID, WOF_ID, VLG_ID, VLG_ID, VLG_ID, VLG_ID, WIH_ID, HNT_ID, SER_ID, CPT_ID
    };

    // logger
    private final static Logger logger = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Autowired
    GameSocketHandler gameSocketHandler;

    @Autowired
    ThreadPoolTaskExecutor gameTaskExecutor;

    // 用户发送的命令转发
    @Override
    public void actionDispatcher(Room room, User user, String playAction, JSONObject messageObject) {
        switch(playAction) {
            case "play-ready":
                this.readyAction(room, user);
                break;
        }
    }

    // 接受用户准备好了的消息
    @Override
    public void readyAction(Room room, User user) {
        boolean allReady = true;
        for(int ii=0; ii<room.getUsers().length; ii++) {
            if (room.getUsers()[ii]==null) {
                allReady = false;
                continue;
            }
            if (room.getUsers()[ii]==user) {
                room.getUsers()[ii].setStatus(1);
            }
            if (room.getUsers()[ii].getStatus()==0) {
                allReady = false;
            }
        }
        // 都准备好了，就开始游戏
        if (allReady) {
            room.setStatus(2);
            this.gameTaskExecutor.execute(new WerewolfGameTask(room));
        }
    }

    // 上行，狼人杀人
    @Override
    public void werewolfAction(Room room, User user) {
    }

    // 上行，预言家查身份
    @Override
    public void seerAction(Room room, User user) {

    }

    // 上行，女巫救人或毒杀
    @Override
    public void witchAction(Room room, User user) {

    }

    // 上行，猎人杀人
    @Override
    public void hunterAction(Room room, User user) {

    }

    // 下发，结果
    @Override
    public void sendResults(Room room, String message) {

    }

    @Override
    public void callGameOver(Room room) {

    }

    // 检查胜利
    private boolean checkVictory() {
        return true;
    }

    // 发送信息
    private void sendMessage(User user, String message) {
        if (user!=null) {
            TextMessage textMessage = new TextMessage(message);
            WebSocketSession socketSession = gameSocketHandler.getSocket(user.getId());
            try {
                socketSession.sendMessage(textMessage);
            }
            catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    // 发送信息
    private void sendMessage(Room room, String message) {
        for(int ii=0; ii<room.getUsers().length; ii++) {
            this.sendMessage(room.getUsers()[ii], message);
        }
    }

    // 延迟一段时间
    private static void delay(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
