package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌-特征关联
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_card_feature_rel")
public class CardFeatureRelDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long cardId;

    private Long featureId;

    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;
}
