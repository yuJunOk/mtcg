package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.ActionRequestDTO;
import com.aris.mtcg.domain.dto.GameCreateDTO;
import com.aris.mtcg.domain.vo.ActionResultVO;
import com.aris.mtcg.domain.vo.GameHistoryVO;
import com.aris.mtcg.domain.vo.GameStateVO;
import com.aris.mtcg.domain.vo.GameStatsVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ReplayVO;

/**
 * 对战服务接口
 *
 * @author pengYuJun
 */
public interface GameService {

    /** 创建对局（FR4.1），返回 gameId */
    Long createGame(Long userId, GameCreateDTO dto);

    /** 查询对局状态（FR4.2），含隐私裁剪 */
    GameStateVO getGameState(Long userId, Long gameId);

    /** 执行操作（FR4.3） */
    ActionResultVO executeAction(Long userId, Long gameId, ActionRequestDTO dto);

    /** 认输（FR4.4） */
    void surrender(Long userId, Long gameId);

    /** 复盘数据（FR4.5） */
    ReplayVO getReplay(Long gameId);

    /** 个人对局历史（FR5.4） */
    PageVO<GameHistoryVO> listHistory(Long userId, int page, int size);

    /** 胜败统计（FR5.4） */
    GameStatsVO getStats(Long userId);
}
