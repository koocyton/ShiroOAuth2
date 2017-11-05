package com.doopp.gauss.server.task;

import com.doopp.gauss.api.message.RoomMessage;

public interface GameTask extends Runnable {

    void roomMessageHandle(RoomMessage message);
}
