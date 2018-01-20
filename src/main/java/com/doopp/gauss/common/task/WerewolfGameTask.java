package com.doopp.gauss.common.task;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

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


    // 所有游戏准备好了后，游戏开始
    @Override
    public void callGameStart(Room room) {
        this.sendMessage(room, "{\"action\":\"game-start\"}");
    }

    // 先随机派发用户身份
    @Override
    public void distributeIdentity(Room room) {
        Random random= new Random();
        String[] identities = this.identities.clone();
        boolean[] bool = new boolean[identities.length];
        User[] wolfUser = new User[]{};
        // 分配身份
        for(int ii=0; ii<identities.length; ii++) {
            int nn = random.nextInt(identities.length);
            if(bool[ii]){
                continue;
            }
            room.getUsers()[ii].setIdentity(identities[nn]);
            // 如果是狼人
            if (identities[nn].equals(WOF_ID)) {
                int z = wolfUser.length;
                wolfUser[z] = room.getUsers()[ii];
            }
            this.sendMessage(room.getUsers()[ii], "{\"\":\"\"}");
            bool[ii]=true;
        }
        // 通知狼的身份
        for(User user : wolfUser) {
            this.sendMessage(user, "{\"action\":\"all-wolf\"}");
        }
    }

    // 下发，进入夜晚
    @Override
    public void enterNight(Room room) {
        this.callWerewolf(room, 40);
        this.callSeer(room, 40);
        this.callWitch(room, 40);
        this.enterDay(room);
    }

    // 下发，进入白天
    @Override
    public void enterDay(Room room) {
        this.sendLastNightResult(room);
        this.callHunter(room);
        if (this.checkVictory()) {
            this.sendResults(room, "");
            return;
        }
        this.callAllSpeak(room);
        this.callVoter(room);
        this.callHunter(room);
        if (this.checkVictory()) {
            this.sendResults(room, "");
            return;
        }
        enterNight(room);
    }

    // 发送昨晚的结果
    private void sendLastNightResult(Room room) {

    }

    // 下发，狼人出来杀人
    private void callWerewolf(Room room, int duration) {
    }

    // 下发，预言家查身份
    private void callSeer(Room room, int duration) {
    }

    // 下发，女巫救人或毒杀
    private void callWitch(Room room, int duration) {
    }

    // 下发，猎人杀人
    private void callHunter(Room room) {
    }

    private void callAllSpeak(Room room) {
        for(User user : room.getUsers()) {
            if (user.isLiving()) {
                this.callOneSpeak(user);
                delay(30);
            }
        }
    }

    private void callVoter(Room room) {
    }

    private void callOneSpeak(User user) {
        playService.sendMessage(user, "{\"action\":\"speak\", \"user\":"+user.getId()+", \"timeLimit\":30}");
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
