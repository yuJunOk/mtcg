package com.aris.mtcg.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * 单方玩家局面视图
 *
 * <p>手牌、盖卡、行动计数器按请求方归属裁剪：本人完整，对手手牌空列表 + handCount， 盖卡隐藏牌面，行动计数器为 null。
 *
 * @author pengYuJun
 */
@Data
public class PlayerStateVO {

    private String playerId;

    /** 先后攻：FIRST / SECOND */
    private String side;

    /** 卡组剩余张数（不暴露具体卡牌） */
    private Integer deckCount;

    /** 冲击卡组剩余张数 */
    private Integer rushDeckCount;

    /** 手牌：本人完整，对手为空列表 */
    private List<CardInstanceVO> hand;

    /** 手牌数量（对手也可见） */
    private Integer handCount;

    /** 时间线（公开） */
    private List<CardInstanceVO> timeline;

    /** 撤退区（公开） */
    private List<CardInstanceVO> retreat;

    /** 虚空区（公开） */
    private List<CardInstanceVO> voidZone;

    /** 场上区域（公开） */
    private FieldZoneVO field;

    /** 基地部署计数（仅本人可见，对手为 null） */
    private Integer baseDeployCount;

    /** 号召计数（仅本人可见，对手为 null） */
    private Integer summonCount;
}
