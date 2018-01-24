package com.doopp.gauss.common.entity;

public class PlayerAction {

    // 动作
    private String action;

    // 行动人
    private Player actionPlayer;

    // 目标用户
    private Player targetPlayer;

    public PlayerAction(String action, Player actionPlayer, Player targetPlayer) {
        this.action = action;
        this.actionPlayer = actionPlayer;
        this.targetPlayer = targetPlayer;
    }
}
