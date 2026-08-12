package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.entity.GameDO;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/**
 * 对局历史条目（FR5.4）
 *
 * <p>{@link #fromDO} 仅填充对局表字段；对手昵称、卡组名、本人结果等由 Service 联查后补齐。
 *
 * @author pengYuJun
 */
@Data
public class GameHistoryVO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long gameId;

    /** 对手昵称（联查 user 表） */
    private String opponentName;

    /** 本人方：PLAYER1 / PLAYER2 */
    private String selfSide;

    /** 本人结果：WIN / LOSE / DRAW / UNFINISHED */
    private String result;

    /** PLAYER1 / PLAYER2 / DRAW / null */
    private String winner;

    private String gameMode;

    private String status;

    /** 本人使用的卡组名（联查 deck 表） */
    private String deckName;

    private String createTime;

    /** 可空 */
    private String endTime;

    /** 从 DO 转换为本类（不含联查字段） */
    public static GameHistoryVO fromDO(GameDO game) {
        if (game == null) {
            return null;
        }
        GameHistoryVO vo = new GameHistoryVO();
        vo.setGameId(game.getId());
        vo.setWinner(game.getWinner());
        vo.setGameMode(game.getGameMode());
        vo.setStatus(game.getStatus());
        vo.setCreateTime(game.getCreateTime() != null ? game.getCreateTime().format(FMT) : null);
        vo.setEndTime(game.getEndTime() != null ? game.getEndTime().format(FMT) : null);
        return vo;
    }
}
