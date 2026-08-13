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

    /** 开局后的对外业务编码；未匹配时为 null */
    private String gameId;

    public static GameMatchVO hit(String gameId) {
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
