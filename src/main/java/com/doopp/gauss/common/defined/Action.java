package com.doopp.gauss.common.defined;

public enum Action {

    WOLF_CHOICE("wolf-choice"),
    VILLAGER_CHOICE("village-choice"),
    WITCH_CHOICE("village-choice"),
    HUNTER_CHOICE("village-choice"),
    SEER_CHOICE("village-choice"),
    CUPID_CHOICE("village-choice");

    String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
