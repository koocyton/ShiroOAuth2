package com.doopp.gauss.api.message;

import lombok.Data;

@Data
public class RoomMessage {

    private String action = "";

    public RoomMessage (String action) {
        this.action = action;
    }
}
