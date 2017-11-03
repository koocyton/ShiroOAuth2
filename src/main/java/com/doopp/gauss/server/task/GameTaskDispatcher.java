package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;
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

    public GameTaskDispatcher(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute(String className, RoomEntity sessionRoom) {
        switch(className) {
            case "WereWolf" :
                this.taskExecutor.execute(new WereWolfGameTask(sessionRoom));
                break;
            case "BattleRoyale" :
                this.taskExecutor.execute(new GuessDrawTask(sessionRoom));
                break;
            case "GuessGraw" :
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
                logger.info(" >>> WereWolfGameTask.run getRoom : " + roomSocket.getRooms());
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

            while(true) {
                logger.info(" >>> GuessDrawTask.run getRooms : " + roomSocket.getRooms());
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

            while(true) {
                logger.info(" >>> BattleRoyaleTask.run getRoom : " + roomSocket.getRooms());
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
