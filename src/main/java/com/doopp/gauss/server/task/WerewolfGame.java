package com.doopp.gauss.server.task;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class WerewolfGame {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AccountService accountService;

    @Autowired
    CustomShadedJedis roomRedis;

    private class GameTask implements Runnable {

        public void run()  {

            // logger.info(" >>> Run GameTask " + userService);
            while(true) {
                if (accountService==null) {
                    continue;
                }
                // UserEntity user = accountService.getUserByToken();
                logger.info(" >>> Run GameTask - AccountService : " + accountService);
                roomRedis.test();
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    private TaskExecutor taskExecutor;

    public WerewolfGame(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.launch();
    }

    private void launch() {
        taskExecutor.execute(new GameTask());
    }
}
