package com.aris.mtcg.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * 场上/区域中的卡牌实例视图
 *
 * <p>盖卡时对手侧仅保留 {@code instanceId} + {@code isFaceDown=true}，隐藏 cardCode/cardName。
 *
 * @author pengYuJun
 */
@Data
public class CardInstanceVO {

    /** 对局内唯一实例 ID */
    private String instanceId;

    private String cardCode;

    private String cardName;

    private Integer level;

    private String color;

    /** 当前战力（受效果影响） */
    private Integer currentPower;

    /** 当前射程 R */
    private Integer currentRange;

    /** 是否盖卡 */
    private Boolean isFaceDown;

    private Boolean enteredThisTurn;

    private Boolean movedThisTurn;

    private Integer attackUsed;

    private Boolean interceptUsed;

    /** 结附卡 */
    private List<CardInstanceVO> attachedCards;
}
