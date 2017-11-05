package com.doopp.gauss.server.task;

import com.alibaba.fastjson.JSONObject;

public interface GameTask extends Runnable {

    void roomMessageHandle(JSONObject messageObject);
}
