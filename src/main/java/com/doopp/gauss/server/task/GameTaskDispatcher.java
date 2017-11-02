package com.doopp.gauss.server.task;

import org.springframework.core.task.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameTaskDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(GameTaskDispatcher.class);

    private final TaskExecutor taskExecutor;

    public GameTaskDispatcher(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void execute() {
        logger.info(" >>> execute(new WereWolfGameTask()) ");
        this.taskExecutor.execute(new WereWolfGameTask());
    }
}
