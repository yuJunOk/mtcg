package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 对局记录数据对象，与 mtcg_game_record 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_game_record")
public class GameDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 对外业务编码（G-xxxxxxxx） */
    private String gameCode;

    /** 发起方用户 ID（不等于先攻；先攻由应用层/引擎决定） */
    private Long player1Id;

    /** 对手方用户 ID */
    private Long player2Id;

    /** player1 使用的卡组 ID */
    private Long deck1Id;

    /** player2 使用的卡组 ID */
    private Long deck2Id;

    /** 胜方：PLAYER1 / PLAYER2 / DRAW；进行中为 null */
    private String winner;

    /** 对局模式：CASUAL / RANKED / AI */
    private String gameMode;

    /** 对局状态：WAITING / IN_PROGRESS / FINISHED */
    private String status;

    /** 最近一次回合结束的完整状态快照（JSON 文本） */
    private String turnSnapshot;

    /** 操作流水 JSON 数组字符串 */
    private String actionLog;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;

    /** 对局结束时间，进行中为 null */
    private LocalDateTime endTime;
}
