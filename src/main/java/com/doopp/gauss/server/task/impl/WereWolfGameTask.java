package com.doopp.gauss.server.task.impl;

/**
 *  协议部分
 *
 *  进入白天  {"action":"toDay"}
 *  进入夜晚  {"action":"toNight"}
 *
 *  狼开始行动  s {"action":"wolfAction"}
 *  狼选择杀人  c {"action":"wolfKill", "player":1, "kill":2}
 *  狼选择杀人  c {"action":"wolfKill", "player":1, "kill":0}
 *
 *  预言家开始行动  s {"action":"seerAction"}
 *  预言家查看  c {"action":"seerCheck", "player":3, "check":3, }
 *  告诉预言家这个人的身份  s {"action":"seerCheck", "player":3, "check":3, "result":"villager"}
 *
 *  女巫开始行动  s {"action":"seerAction"}
 *  女巫救人  c {"action":"seerRescue", "player":3, "check":3,}
 *  女巫下毒  c {"action":"seerPoison", "player":3, "check":3, "result":"villager"}
 *
 *  平安夜   s  {"action":"safelyNight"}
 *  有人被杀 s  {"action":"killingNight", "players":[3,4]}
 *
 *  触发猎人技能 s {"action":"hunterAction"}
 *  猎人射击 c {"action":"hunterShot", "players":4, "kill":5}
 *
 *  触发猎人技能 s {"action":"hunterAction"}
 *  猎人射击 c {"action":"hunterShot", "players":4, "kill":5}
 *
 *  发言 s {"action":"talkStage", "player":2, "time":30}
 *  跳过发言 c {"action":"talkStage", "player":2, "time":30}
 *
 *  开始投票 s {"action":"voteAction"}
 *  投票给 c {"action":"voteKill"}
 *  放弃投票 c {"action":"voteWaiver"}
 *
 *  有人被票决  {"action":"ticketKill", "players":[3,4]}
 *  平票  {"action":"flatTicket", "players":[3,4]}
 *
 */

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.server.task.GameTask;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.util.Random;

public class WereWolfGameTask implements GameTask {

    private final Logger logger = LoggerFactory.getLogger(WereWolfGameTask.class);

    private final RoomEntity sessionRoom;

    private final RoomSocketHandler roomSocketHandler;

    private final String[] identitys = new String[] {
            "wolf", "wolf", "wolf", "wolf",
            "villager", "villager", "villager", "villager",
            "witch", "hunter", "seer", "cupit"
    };

    public WereWolfGameTask (RoomSocketHandler roomSocketHandler, RoomEntity sessionRoom) {
        this.sessionRoom = sessionRoom;
        this.roomSocketHandler = roomSocketHandler;
        this.sessionRoom.setGameTask(this);
    }

    // 开始执行
    public void run() {
        int loopNumber = 0;
        while(true) {
            delay(1000);
            if (this.allReady(++loopNumber)) {
                this.gameStart();
                break;
            }
        }
    }

    // 游戏开始
    private void gameStart() {
        this.assignIdentity();
    }

    // 抢身份
    private void grabIdentityHandle() {
    }

    // 分配身份
    private void assignIdentity() {
        Random random= new Random();
        String[] _identitys = this.identitys.clone();
        for(int ii=0; ii<this.identitys.length; ii++) {
            int nn = random.nextInt(this.identitys.length);
            this.identitys[ii] = _identitys[nn];
        }
        this.messageToAll(JSONObject.toJSONString(this.identitys));
        this.toBegin();
    }

    // 提示狼杀人
    private void wolfAction() {

        this.messageToAll(JSONObject.toJSONString(new Object()));
    }

    // 提示女巫救人
    private void witchAction() {

    }

    // 提示预言家查询身份
    private void seerAction() {

    }

    private void toBegin() {
        toNight();
    }

    // 提示天亮
    private void toDay() {
        toNight();
    }

    // 天黑了
    private void toNight() {
        this.wolfAction();
        this.seerAction();
        toDay();
    }

    // 接受房间里传来的消息
    // 已经将非游戏内的用户的信息过滤
    @Override
    public void roomMessageHandle(JSONObject messageObject) {
        String action = messageObject.getString("action");
        if (Strings.isNullOrEmpty(action)) {
            return;
        }
        if (action.equals("publicTalk")) {
            this.messageToAll(messageObject.toJSONString());
        }
    }

    // 发送消息到房间
    private void messageToAll(String message) {
        this.roomSocketHandler.roomGameTalk(this.sessionRoom, new TextMessage(message));
    }

    // 玩家都准备好了
    private boolean allReady(int loopNumber) {
        return loopNumber>=10;
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
