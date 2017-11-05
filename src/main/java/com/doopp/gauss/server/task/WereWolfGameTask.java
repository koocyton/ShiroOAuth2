package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

public class WereWolfGameTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WereWolfGameTask.class);

    private final RoomEntity sessionRoom;

    private final RoomSocketHandler roomSocketHandler;

    WereWolfGameTask (RoomSocketHandler roomSocketHandler, RoomEntity sessionRoom) {
        this.sessionRoom = sessionRoom;
        this.roomSocketHandler = roomSocketHandler;
    }

    // 开始执行
    public void run() {
        this.gameStart();
    }

    // 游戏开始
    private void gameStart() {
        while(true) {
            delay(1000);
            this.messageToAll("大家好");
        }
    }

    // 抢身份
    private void grabIdentityHandle() {
    }

    // 分配身份
    private void assignIdentity() {
    }

    // 提示狼杀人
    private void wolfKilling() {
    }

    // 提示女巫救人
    private void witchHelp() {

    }

    // 提示预言家查询身份
    private void seerViewIdentity() {

    }

    // 提示天亮
    private void toDay() {

    }

    // 天黑了
    private void toNight() {

    }


    // 接受房间里传来的消息
    private void roomMessageHandle(String message) {

    }

    // 发送消息到房间
    private void messageToAll(String message) {
        this.roomSocketHandler.roomGameTalk(this.sessionRoom, new TextMessage(message));
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
