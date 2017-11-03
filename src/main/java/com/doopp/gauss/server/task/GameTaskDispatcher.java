package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.game.impl.BattleRoyaleGame;
import com.doopp.gauss.api.game.impl.GuessDrawGame;
import com.doopp.gauss.api.game.impl.WereWolfGame;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameTaskDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(GameTaskDispatcher.class);

    private final TaskExecutor taskExecutor;

    @Autowired
    RoomSocketHandler roomSocket;

    @Autowired
    private WereWolfGame wereWolfGame;

    @Autowired
    private BattleRoyaleGame battleRoyaleGame;

    @Autowired
    private GuessDrawGame guessDrawGame;

    public GameTaskDispatcher(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute(int gameType, RoomEntity sessionRoom) {
        switch(gameType) {
            case RoomEntity.WERE_WOLF_GAME :
                this.taskExecutor.execute(new WereWolfGameTask(sessionRoom));
                break;
            case RoomEntity.GUESS_DRAW_GAME :
                this.taskExecutor.execute(new GuessDrawTask(sessionRoom));
                break;
            case RoomEntity.BATTLE_ROYALE_GAME :
                this.taskExecutor.execute(new BattleRoyaleTask(sessionRoom));
                break;
        }
    }

    /*
     * 狼人杀
     */
    private class WereWolfGameTask implements Runnable {

        RoomEntity sessionRoom;

        WereWolfGameTask (RoomEntity sessionRoom) {
            this.sessionRoom = sessionRoom;
        }

        public void run() {

            while(true) {
                wereWolfGame.handleDaemonMessage(new DaemonMessage());
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 你画我猜
     */
    private class GuessDrawTask implements Runnable {

        RoomEntity sessionRoom;

        GuessDrawTask (RoomEntity sessionRoom) {
            this.sessionRoom = sessionRoom;
        }

        public void run() {
            guessDrawGame.handleDaemonMessage(new DaemonMessage());
            while(true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 大逃杀
     */
    private class BattleRoyaleTask implements Runnable {

        RoomEntity sessionRoom;

        BattleRoyaleTask (RoomEntity sessionRoom) {
            this.sessionRoom = sessionRoom;
        }

        public void run() {
            battleRoyaleGame.handleDaemonMessage(new DaemonMessage());
            while(true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
