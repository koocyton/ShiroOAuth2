package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;

public class WereWolfGameTask implements Runnable {

    private RoomEntity sessionRoom;

    WereWolfGameTask (RoomEntity sessionRoom) {
        this.sessionRoom = sessionRoom;
    }

    public void run() {
        delay(100);
        this.gameStart();
    }

    private void gameStart() {
        delay(1000);
    }

    // 开始分派身份

    // 提示狼杀人

    // 提示女巫救人

    // 提示预言家查询身份

    // 提示天亮

    // 提示

    // 延迟的时间
    private static void delay(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
