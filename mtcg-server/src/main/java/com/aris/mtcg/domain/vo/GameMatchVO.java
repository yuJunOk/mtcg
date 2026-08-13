package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 在线匹配结果
 *
 * @author pengYuJun
 */
@Data
public class GameMatchVO {

    /** 是否匹配到对手并已开局 */
    private boolean matched;

    /** 开局后的对局 ID；未匹配时为 null */
    private Long gameId;

    public static GameMatchVO hit(Long gameId) {
        GameMatchVO vo = new GameMatchVO();
        vo.setMatched(true);
        vo.setGameId(gameId);
        return vo;
    }

    public static GameMatchVO miss() {
        GameMatchVO vo = new GameMatchVO();
        vo.setMatched(false);
        vo.setGameId(null);
        return vo;
    }
}
