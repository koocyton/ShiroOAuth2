package com.doopp.gauss.common.task;

import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class WerewolfGameTask implements Runnable {

    private final Identity[] identities = {
        Identity.WOLF, Identity.WOLF, Identity.WOLF,
        Identity.VILLAGER, Identity.VILLAGER, Identity.VILLAGER,
        Identity.SEER, Identity.WITCH, Identity.HUNTER
    };

    // logger
    private final static Logger logger = LoggerFactory.getLogger(WerewolfGameTask.class);

    private final static PlayService playService = (PlayService) ApplicationContextUtil.getBean("playService");

    // 这个线程处理的房间
    private final Room room;

    public WerewolfGameTask (Room room) {
        this.room = room;
    }

    public void run() {
        this.callGameStart(room);
        this.distributeIdentity(room);
        this.enterNight(room);
    }

    // 所有游戏准备好了后，游戏开始
    private void callGameStart(Room room) {
        playService.sendMessage(room, "{\"action\":\"game-start\"}");
        playService.delay(5);
    }

    // 先随机派发用户身份
    private void distributeIdentity(Room room) {
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
            playService.sendMessage(room.getUsers()[ii], "{\"action\":\"distribute-identity\", \"identity\":\"" + room.getUsers()[ii].getIdentity() + "\"}");
            bool[ii]=true;
        }
        // 通知狼的身份
        for(User user : wolfUser) {
            playService.sendMessage(user, "{\"action\":\"wolf-identity\", \"users\":[1,2,3]}");
        }
        //
        playService.delay(3);
    }

    // 下发，进入夜晚
    private void enterNight(Room room) {
        this.callWerewolf(room);
        this.callSeer(room);
        this.callWitch(room);
        this.enterDay(room);
    }

    // 下发，进入白天
    private void enterDay(Room room) {
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
    private void callWerewolf(Room room) {
        User[] users = room.getUsers();
        User[] wolfs = new User[]{};
        int ii = 0;
        // 拿到谁是狼
        for(User user : users) {
            if (user!=null && user.getIdentity().equals(WOF_ID)) {
                wolfs[ii++] = user;
            }
        }
        // 狼开始行动
        for(User user : wolfs) {
            playService.sendMessage(user, "{\"action\":\"wolf-killings\"");
        }
        int maxLoopNumber = 40;
        int nowLoopNumber = 0;
        // 遍历检擦狼是否执行完成
        while(true) {
            playService.delay(1);
            boolean allAction = true;
            for(User user : wolfs) {
                if (user.getAction == false) {
                    allAction = false;
                }
            }
            nowLoopNumber++;
            if (allAction || nowLoopNumber>maxLoopNumber) {
                this.callWitch(room);
            }
        }
    }

    // 下发，预言家查身份
    private void callSeer(Room room) {
        playService.delay(40);
    }

    // 下发，女巫救人或毒杀
    private void callWitch(Room room) {
        playService.delay(40);
    }

    // 下发，猎人杀人
    private void callHunter(Room room) {
        playService.delay(40);
    }



    // 下发，结果
    private void sendResults(Room room, String message) {

    }

    // 下发，游戏结束
    private void callGameOver(Room room) {

    }

    // 检查胜利
    private boolean checkVictory() {
        return true;
    }

    private void callAllSpeak(Room room) {
        for(User user : room.getUsers()) {
            if (user.isLiving()) {
                this.callOneSpeak(user);
                playService.delay(30);
            }
        }
    }

    private void callVoter(Room room) {
    }

    private void callOneSpeak(User user) {
        playService.sendMessage(user, "{\"action\":\"speak\", \"user\":"+user.getId()+", \"timeLimit\":30}");
    }
}
