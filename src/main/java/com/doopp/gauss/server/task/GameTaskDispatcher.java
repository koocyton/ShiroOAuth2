package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.server.task.impl.WereWolfGameTask;
import com.doopp.gauss.server.websocket.RoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class GameTaskDispatcher {

    private final TaskExecutor taskExecutor;

    @Autowired
    RoomSocketHandler roomSocketHandler;

    public GameTaskDispatcher(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute(int gameType, RoomEntity sessionRoom) {

        switch(gameType) {
            case RoomEntity.WERE_WOLF_GAME :
                this.taskExecutor.execute(new WereWolfGameTask(roomSocketHandler, sessionRoom));
                break;
            //case RoomEntity.GUESS_DRAW_GAME :
            //    this.taskExecutor.execute(new GuessDrawTask());
            //    break;
            //case RoomEntity.BATTLE_ROYALE_GAME :
            //    this.taskExecutor.execute(new BattleRoyaleTask());
            //    break;
        }
    }
}
