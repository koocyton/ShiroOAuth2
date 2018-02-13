package com.doopp.gauss.common.entity;

import lombok.Data;

@Data
public class PlayerAction {

    // 动作
    private String action;

    // 行动人
    private Long actionPlayer;

    // 目标用户
    private Long targetPlayer;

    public PlayerAction(String action, Long actionPlayer, Long targetPlayer) {
        this.action = action;
        this.actionPlayer = actionPlayer;
        this.targetPlayer = targetPlayer;
    }
}
