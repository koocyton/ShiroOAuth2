package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;

public class WereWolfGameTask implements Runnable {

    private RoomEntity sessionRoom;

    WereWolfGameTask (RoomEntity sessionRoom) {
        this.sessionRoom = sessionRoom;
    }

    // 开始执行
    public void run() {
        delay(100);
        this.gameStart();
    }

    // 游戏开始
    private void gameStart() {
        delay(1000);
    }

    // 抢身份
    private void grabIdentityHandle() {
    }

    // 分配身份
    private void assignIdentity() {
    }

    // 提示狼杀人
    private void assignIdentity() {
    }

    // 提示女巫救人

    // 提示预言家查询身份

    // 提示天亮

    // 提示


    // 接受房间里传来的消息
    private void roomMessageHandle(String message) {

    }

    // 发送消息到房间
    private void sendMessage2Player(String message) {

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
