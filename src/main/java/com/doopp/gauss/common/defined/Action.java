package com.doopp.gauss.common.defined;

public class Action {

    // 进入房间
    public static final String JOIN_ROOM = "join-room";

    // 空等
    public static final String ONLY_WAIT    = "only-wait";

    // 拒接 Action
    public static final String REJECT_ACTION    = "reject-action";

    // 拿到所有的房间内的用户信息
    public static final String ALL_PLAYER    = "all-player";

    // 玩家加入
    public static final String PLAYER_JOIN   = "player-join";

    // 玩家离开
    public static final String PLAYER_LEAVE  = "player-leave";

    // 玩家准备
    public static final String PLAYER_READY  = "player-ready";

    // 游戏开始
    public static final String GAME_START    = "game-start";

    // 玩家的身份
    public static final String PLAYER_IDENTITY = "player-identity";

    // 狼的身份
    public static final String WOLF_IDENTITY   = "wolf-identity";

    // 玩家说话
    public static final String PLAYER_SPEAK  = "player-speak";

    // 玩家投票
    public static final String PLAYER_VOTE   = "player-vote";

    // 下行
    public static final String WOLF_CALL     = "wolf-call";
    public static final String VILLAGER_CALL = "village-call";
    public static final String WITCH_CALL    = "witch-call";
    public static final String HUNTER_CALL   = "hunter-call";
    public static final String SEER_CALL     = "seer-call";
    public static final String CUPID_CALL    = "cupid-call";

    // 上行
    public static final String WOLF_CHOICE     = "wolf-choice";
    public static final String VILLAGER_CHOICE = "village-choice";
    public static final String WITCH_CHOICE    = "witch-choice";
    public static final String HUNTER_CHOICE   = "hunter-choice";
    public static final String SEER_CHOICE     = "seer-choice";
    public static final String CUPID_CHOICE    = "cupid-choice";

    public static final String WOLF_KILL     = "wolf-kill";
    public static final String VILLAGER_KILL = "village-kill";

    // 总结
    public static final String SHOW_RESULT     = "show-result";

    // 显示平安夜
    public static final String SHOW_SAFE_NIGHT = "show-safe-night";
    // 显示有人被杀
    public static final String SHOW_PLAYER_DIE = "show-player-die";
}
