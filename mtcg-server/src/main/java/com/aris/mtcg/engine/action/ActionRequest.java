package com.aris.mtcg.engine.action;

import com.aris.mtcg.engine.enums.Zone;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作请求（由 API 层装配，引擎不感知 HTTP）。
 *
 * <p>所有可选参数通过 Map 传递，由各 Handler 自行解析，避免子类爆炸。
 *
 * @author pengYuJun
 */
public class ActionRequest {

    /** 对局 ID */
    private String gameId;

    /** 操作发起者玩家 ID */
    private String playerId;

    /** 操作类型 */
    private ActionType type;

    /** 主体卡编号（手牌/场上卡），可空 */
    private String cardCode;

    /** 源区域，可空 */
    private Zone sourceZone;

    /** 源区域下标（侧翼/基地多格），可空 */
    private int sourceIndex;

    /** 目标区域，可空 */
    private Zone targetZone;

    /** 目标区域下标，可空 */
    private int targetIndex;

    /** 目标卡编号（如结附父卡、攻击目标），可空 */
    private String targetCardCode;

    /** 扩展参数（如调整位置的互换对列表） */
    private Map<String, Object> extras = new HashMap<>();

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public Zone getSourceZone() {
        return sourceZone;
    }

    public void setSourceZone(Zone sourceZone) {
        this.sourceZone = sourceZone;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public Zone getTargetZone() {
        return targetZone;
    }

    public void setTargetZone(Zone targetZone) {
        this.targetZone = targetZone;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public String getTargetCardCode() {
        return targetCardCode;
    }

    public void setTargetCardCode(String targetCardCode) {
        this.targetCardCode = targetCardCode;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras != null ? extras : new HashMap<>();
    }
}
