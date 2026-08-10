package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.CardFeatureCreateDTO;
import com.aris.mtcg.domain.entity.CardFeatureDO;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌特征展示对象
 *
 * @author pengYuJun
 */
@Data
public class CardFeatureVO {

    private Long id;

    /** 特征编码 */
    private String code;

    /** 特征名称 */
    private String name;

    /** 背景颜色 */
    private String bgColor;

    private LocalDateTime createTime;

    // ========== 静态工厂方法 ==========

    public static CardFeatureVO fromDO(CardFeatureDO entity) {
        if (entity == null) return null;
        CardFeatureVO vo = new CardFeatureVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setBgColor(entity.getBgColor());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    public static CardFeatureVO fromDTO(CardFeatureCreateDTO dto) {
        if (dto == null) return null;
        CardFeatureVO vo = new CardFeatureVO();
        vo.setCode(dto.getCode());
        vo.setName(dto.getName());
        vo.setBgColor(dto.getBgColor());
        return vo;
    }

    public static CardFeatureDO toDO(CardFeatureVO vo) {
        if (vo == null) return null;
        CardFeatureDO entity = new CardFeatureDO();
        entity.setId(vo.getId());
        entity.setCode(vo.getCode());
        entity.setName(vo.getName());
        entity.setBgColor(vo.getBgColor());
        return entity;
    }
}
