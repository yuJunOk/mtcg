package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌收藏数据对象，与 mtcg_card_collection 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_card_collection")
public class CardCollectionDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 归属用户 */
    private Long userId;

    /** 卡牌编号 */
    private String cardCode;

    /** 拥有数量 */
    private Integer quantity;

    /** 用户自定义标签（逗号分隔） */
    private String tags;

    /** 个人备注 */
    private String note;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
