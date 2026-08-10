package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.CardCreateDTO;
import com.aris.mtcg.domain.entity.CardDO;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌展示对象
 *
 * @author pengYuJun
 */
@Data
public class CardVO {

    private Long id;

    private String cardCode;

    private String productCode;

    private String cardName;

    private String cardType;

    private Short level;

    private String color;

    private String environment;

    private String traits;

    private Short attackRange;

    private Short power;

    private String rarity;

    private String effectText;

    private String effectJson;

    private String imagePath;

    private LocalDateTime createTime;

    // ========== 静态工厂方法 ==========

    /** 从 DO 转换为 VO */
    public static CardVO fromDO(CardDO card) {
        if (card == null) {
            return null;
        }
        CardVO vo = new CardVO();
        vo.setId(card.getId());
        vo.setCardCode(card.getCardCode());
        vo.setProductCode(card.getProductCode());
        vo.setCardName(card.getCardName());
        vo.setCardType(card.getCardType());
        vo.setLevel(card.getLevel());
        vo.setColor(card.getColor());
        vo.setEnvironment(card.getEnvironment());
        vo.setTraits(card.getTraits());
        vo.setAttackRange(card.getAttackRange());
        vo.setPower(card.getPower());
        vo.setRarity(card.getRarity());
        vo.setEffectText(card.getEffectText());
        vo.setEffectJson(card.getEffectJson());
        vo.setImagePath(card.getImagePath());
        vo.setCreateTime(card.getCreateTime());
        return vo;
    }

    /** 从 DTO 转换为 VO */
    public static CardVO fromDTO(CardCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        CardVO vo = new CardVO();
        vo.setCardCode(dto.getCardCode());
        vo.setProductCode(dto.getProductCode());
        vo.setCardName(dto.getCardName());
        vo.setCardType(dto.getCardType());
        vo.setLevel(dto.getLevel());
        vo.setColor(dto.getColor());
        vo.setEnvironment(dto.getEnvironment());
        vo.setTraits(dto.getTraits());
        vo.setAttackRange(dto.getAttackRange());
        vo.setPower(dto.getPower());
        vo.setRarity(dto.getRarity());
        vo.setEffectText(dto.getEffectText());
        vo.setEffectJson(dto.getEffectJson());
        vo.setImagePath(dto.getImagePath());
        return vo;
    }

    /** 转换为 DO */
    public static CardDO toDO(CardVO vo) {
        if (vo == null) {
            return null;
        }
        CardDO card = new CardDO();
        card.setId(vo.getId());
        card.setCardCode(vo.getCardCode());
        card.setProductCode(vo.getProductCode());
        card.setCardName(vo.getCardName());
        card.setCardType(vo.getCardType());
        card.setLevel(vo.getLevel());
        card.setColor(vo.getColor());
        card.setEnvironment(vo.getEnvironment());
        card.setTraits(vo.getTraits());
        card.setAttackRange(vo.getAttackRange());
        card.setPower(vo.getPower());
        card.setRarity(vo.getRarity());
        card.setEffectText(vo.getEffectText());
        card.setEffectJson(vo.getEffectJson());
        card.setImagePath(vo.getImagePath());
        return card;
    }

    /** 转换为 DTO */
    public static CardCreateDTO toDTO(CardVO vo) {
        if (vo == null) {
            return null;
        }
        CardCreateDTO dto = new CardCreateDTO();
        dto.setCardCode(vo.getCardCode());
        dto.setProductCode(vo.getProductCode());
        dto.setCardName(vo.getCardName());
        dto.setCardType(vo.getCardType());
        dto.setLevel(vo.getLevel());
        dto.setColor(vo.getColor());
        dto.setEnvironment(vo.getEnvironment());
        dto.setTraits(vo.getTraits());
        dto.setAttackRange(vo.getAttackRange());
        dto.setPower(vo.getPower());
        dto.setRarity(vo.getRarity());
        dto.setEffectText(vo.getEffectText());
        dto.setEffectJson(vo.getEffectJson());
        dto.setImagePath(vo.getImagePath());
        return dto;
    }
}
