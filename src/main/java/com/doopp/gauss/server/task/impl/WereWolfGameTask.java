package com.doopp.gauss.server.task.impl;

/**
 *  协议部分
 *
 *  进入白天  {"action":"toDay"}
 *  进入夜晚  {"action":"toNight"}
 *
 *  狼开始行动  s {"action":"wolfAction"}
 *  狼选择杀人  c {"action":"wolfAction", "kill":1, "kill":2}
 *
 *  预言家开始行动  s {"action":"seerAction"}
 *  预言家查看  c {"action":"seerAction", "check":3, }
 *  告诉预言家这个人的身份  s {"action":"identityRevealing", "identity":"villager"}
 *
 *  女巫开始行动  s {"action":"seerAction"}
 *  女巫行动  c {"action":"seerAction", "help":3, "kill":3,}
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
import com.doopp.gauss.server.task.GameTask;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.util.Random;

public class WereWolfGameTask implements GameTask {

    private final static Logger logger = LoggerFactory.getLogger(WereWolfGameTask.class);

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
        this.grabIdentityTrigger();
    }

    // 开始抢身份
    private void grabIdentityTrigger() {
        JSONObject grabIdentity = new JSONObject();
        grabIdentity.put("action", "grabIdentity");
        grabIdentity.put("identity", new String[]{"identity", "wolf"});
        this.messageToAll(grabIdentity.toJSONString());
    }

    // 返回抢身份信息
    private void grabIdentityListen(JSONObject messageObject) {
        this.assignIdentity();
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
    private void wolfActionTrigger() {
        JSONObject wolfAction = new JSONObject();
        wolfAction.put("action", "wolfAction");
        this.messageToAll(wolfAction.toJSONString());
    }

    // 提示女巫行动
    private void witchActionTrigger() {
        JSONObject seerAction = new JSONObject();
        seerAction.put("action", "seerAction");
        this.messageToAll(seerAction.toJSONString());
    }

    // 提示预言家查询身份
    private void seerActionTrigger() {
        JSONObject seerAction = new JSONObject();
        seerAction.put("action", "seerAction");
        this.messageToAll(seerAction.toJSONString());
    }

    // 狼杀人监听
    private void wolfActionListen(JSONObject messageObject) {
        // 狼人杀人完毕，触发女巫开始行动
        this.witchActionTrigger();
    }

    // 女巫活动监听
    private void witchActionListen(JSONObject messageObject) {
    }

    // 提示预言家查询身份
    private void seerActionListen(JSONObject messageObject) {
        messageObject.getString("checkNumber");
    }

    private void toBegin() {
        toNight();
    }

    // 提示天亮
    private void toDay() {
        JSONObject toDay = new JSONObject();
        toDay.put("action", "toDay");
        this.messageToAll(toDay.toJSONString());
        toNight();
    }

    // 天黑了
    private void toNight() {
        JSONObject toNight = new JSONObject();
        toNight.put("action", "toNight");
        this.messageToAll(toNight.toJSONString());
        delay(5000);
        this.wolfActionTrigger();
        this.seerActionTrigger();
    }

    // 接受房间里传来的消息
    // 已经将非游戏内的用户的信息过滤
    @Override
    public void roomMessageHandle(JSONObject messageObject) {
        String action = messageObject.getString("action");
        if (Strings.isNullOrEmpty(action)) {
            return;
        }
        switch(action) {
            // 公共频道说话
            case "publicTalk":
                this.messageToAll(messageObject.toJSONString());
                break;
            // 狼人行动
            case "wolfAction":
                this.wolfActionListen(messageObject);
                break;
            // 女巫行动
            case "witchAction":
                this.witchActionListen(messageObject);
                break;
            // 预言家行动
            case "seerAction":
                this.seerActionListen(messageObject);
                break;
        }
    }

    // 发送消息到房间
    private void messageToAll(String message) {
        this.roomSocketHandler.roomGameTalk(this.sessionRoom, new TextMessage(message));
    }

    // 玩家都准备好了
    private boolean allReady(int loopNumber) {
        return loopNumber>=1;
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
