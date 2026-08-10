package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌特征数据对象，与 mtcg_card_feature 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_card_feature")
public class CardFeatureDO {

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 特征编码（唯一） */
    private String code;

    /** 特征名称 */
    private String name;

    /** 背景颜色（Element Plus 颜色值） */
    private String bgColor;

    /** 创建时间 */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
