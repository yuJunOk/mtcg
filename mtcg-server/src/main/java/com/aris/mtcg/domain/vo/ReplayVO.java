package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.entity.GameDO;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Data;

/**
 * 复盘回放视图（FR4.5）
 *
 * <p>{@link #fromDO} 填充对局元数据；{@code actions} 由 Service 解析 action_log 后设置。
 *
 * @author pengYuJun
 */
@Data
public class ReplayVO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 对外业务编码（G-xxxxxxxx） */
    private String gameId;

    private Long player1Id;

    private Long player2Id;

    private String winner;

    private String gameMode;

    private String createTime;

    private String endTime;

    /** 按时序排列的操作流水 */
    private List<ActionReplayEntryVO> actions;

    /** 从 DO 转换为本类（不含 actions） */
    public static ReplayVO fromDO(GameDO game) {
        if (game == null) {
            return null;
        }
        ReplayVO vo = new ReplayVO();
        vo.setGameId(game.getGameCode());
        vo.setPlayer1Id(game.getPlayer1Id());
        vo.setPlayer2Id(game.getPlayer2Id());
        vo.setWinner(game.getWinner());
        vo.setGameMode(game.getGameMode());
        vo.setCreateTime(game.getCreateTime() != null ? game.getCreateTime().format(FMT) : null);
        vo.setEndTime(game.getEndTime() != null ? game.getEndTime().format(FMT) : null);
        return vo;
    }
}
