package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class WerewolfKillGame {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /// private UserService userService;

    private TaskExecutor taskExecutor;

    @Autowired
    public WerewolfKillGame(TaskExecutor taskExecutor) {
        // this.userService = userService;
        this.taskExecutor = taskExecutor;
        this.launchGame();
    }

    private void launchGame() {
        taskExecutor.execute(new GameTask());
    }

    private class GameTask implements Runnable {

        public void run()  {

            // logger.info(" >>> Run GameTask " + userService);
//            while(true) {
//                if (userService==null) {
//                    continue;
//                }
//                UserEntity user = userService.getUserInfo("koocyton@gmail.com");
//                logger.info(" >>> Run GameTask " + user);
//                try {
//                    Thread.sleep(1000);
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
        }

    }
}
