package com.doopp.gauss.common.task;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WerewolfGameTask implements Runnable {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(WerewolfGameTask.class);

    private final static PlayService playService = (PlayService) ApplicationContextUtil.getBean("playService");

    @Autowired
    private final Room room;

    public WerewolfGameTask (Room room) {
        this.room = room;
    }

    public void run() {
        playService.callGameStart(room);
        delay(5000);
        playService.distributeIdentity(room);
        delay(3000);
        playService.enterNight(room);
        while(true) {
            delay(1000);
            if (true) {
                playService.enterDay(room);
            }
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
