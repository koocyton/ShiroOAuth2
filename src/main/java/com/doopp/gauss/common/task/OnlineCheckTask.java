package com.doopp.gauss.common.task;

import com.doopp.gauss.common.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

// 用来判断用户是否在线的处理
// 如果用户处理超时，运行房间游戏的线程
// 将用户 ID 记录到  PlayerServiceImpl.doubtPlayers
// 并通知用户，要求用户 10 秒内操作
// 用户操作后，PlayerServiceImpl.doubtPlayers 记录去除
// OnlineCheckTask 检查 PlayerServiceImpl.doubtPlayers
// 的时间戳，超过 10 秒的就将用户数据从房间里删除
// 并将用户的连接关闭，同时，记录下用户
// 是用户在 3 分钟内不能再次登录
public class OnlineCheckTask {

    private class CheckTask implements Runnable {

        @Autowired
        private PlayerService playerService;

        public void run()  {

            while(true) {
                if (playerService==null) {
                    this.sleep(5);
                    continue;
                }

                // Player[] doubtPlayers = playerService.getDoubtPlayers();

                this.sleep(1);
            }
        }

        private void sleep(int second) {
            try {
                Thread.sleep(second * 1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private TaskExecutor taskExecutor;

    public OnlineCheckTask(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.launch();
    }

    private void launch() {
        taskExecutor.execute(new CheckTask());
    }
}
