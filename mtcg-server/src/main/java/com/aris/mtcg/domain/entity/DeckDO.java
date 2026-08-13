package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡组数据对象，与 mtcg_deck 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_deck")
public class DeckDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 归属用户 */
    private Long userId;

    /** 对外业务编码（D-xxxxxxxx） */
    private String deckCode;

    /** 卡组名称 */
    private String deckName;

    /** 主卡组有序条目 JSON：[{cardCode, quantity}, ...] */
    private String mainDeckCodes;

    /** 冲击卡组有序条目 JSON */
    private String rushDeckCodes;

    /** 最近一次校验结果（缓存；与 status 同步） */
    private Boolean isValid;

    /** 卡组状态：READY=可用 / DRAFT=草稿（与 {@link #isValid} 同步，由校验自动写入） */
    private String status;

    /** 是否公开可见 */
    private Boolean isPublic;

    /** 是否允许他人复制 */
    private Boolean isCopyable;

    /** 用户自定义排序（同用户内越小越靠前） */
    private Integer sortOrder;

    /** 用户自定义标签（逗号分隔） */
    private String tags;

    /** 封面卡编号（须在卡组内） */
    private String coverCardCode;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
