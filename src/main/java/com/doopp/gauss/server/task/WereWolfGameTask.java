package com.doopp.gauss.server.task;

import com.doopp.gauss.api.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class WereWolfGameTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(WereWolfGameTask.class);

    @Autowired
    AccountService accountService;

    public void run()  {

        while(true) {
            // UserEntity user = accountService.getUserByToken();
            logger.info(" >>> Run WereWolfGameTask - AccountService : " + accountService);
            if (accountService==null) {
                continue;
            }
            //roomRedis.test();
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
