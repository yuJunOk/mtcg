package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.CardCollectionDTO;
import com.aris.mtcg.domain.entity.CardCollectionDO;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/**
 * 收藏展示对象
 *
 * @author pengYuJun
 */
@Data
public class CardCollectionVO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;

    private Long userId;

    private String cardCode;

    /** 联查 card 表得到（可空，卡牌被删时为 null） */
    private String cardName;

    private Integer quantity;

    private String tags;

    private String note;

    private String createTime;

    private String updateTime;

    // ========== 静态工厂方法 ==========

    /** 从 DO 转换为 VO（不含 cardName） */
    public static CardCollectionVO fromDO(CardCollectionDO collection) {
        if (collection == null) {
            return null;
        }
        CardCollectionVO vo = new CardCollectionVO();
        vo.setId(collection.getId());
        vo.setUserId(collection.getUserId());
        vo.setCardCode(collection.getCardCode());
        vo.setQuantity(collection.getQuantity());
        vo.setTags(collection.getTags());
        vo.setNote(collection.getNote());
        vo.setCreateTime(
                collection.getCreateTime() != null ? collection.getCreateTime().format(FMT) : null);
        vo.setUpdateTime(
                collection.getUpdateTime() != null ? collection.getUpdateTime().format(FMT) : null);
        return vo;
    }

    /** 从 DTO 转换为本类 */
    public static CardCollectionVO fromDTO(CardCollectionDTO dto) {
        if (dto == null) {
            return null;
        }
        CardCollectionVO vo = new CardCollectionVO();
        vo.setCardCode(dto.getCardCode());
        vo.setQuantity(dto.getQuantity());
        vo.setTags(dto.getTags());
        vo.setNote(dto.getNote());
        return vo;
    }

    /** 转换为 DO */
    public static CardCollectionDO toDO(CardCollectionVO vo) {
        if (vo == null) {
            return null;
        }
        CardCollectionDO collection = new CardCollectionDO();
        collection.setId(vo.getId());
        collection.setUserId(vo.getUserId());
        collection.setCardCode(vo.getCardCode());
        collection.setQuantity(vo.getQuantity());
        collection.setTags(vo.getTags());
        collection.setNote(vo.getNote());
        return collection;
    }
}
