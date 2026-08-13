package com.aris.mtcg.controller.game;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.ActionRequestDTO;
import com.aris.mtcg.domain.dto.GameCreateDTO;
import com.aris.mtcg.domain.dto.GameJoinDTO;
import com.aris.mtcg.domain.vo.ActionResultVO;
import com.aris.mtcg.domain.vo.GameHistoryVO;
import com.aris.mtcg.domain.vo.GameMatchVO;
import com.aris.mtcg.domain.vo.GameStateVO;
import com.aris.mtcg.domain.vo.GameStatsVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ReplayVO;
import com.aris.mtcg.service.GameService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对战 REST API
 *
 * <p>路径前缀 {@code /games}；context-path 已含 {@code /api}。 {@code /history}、{@code /stats} 须写在 {@code
 * /{id}} 之前，避免被路径变量吞掉。
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/games")
public class GameController {

    @Resource private GameService gameService;

    /** 个人对局历史（FR5.4） */
    @GetMapping("/history")
    public Result<PageVO<GameHistoryVO>> listHistory(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(gameService.listHistory(userId, page, size));
    }

    /** 胜败统计（FR5.4） */
    @GetMapping("/stats")
    public Result<GameStatsVO> getStats(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId) {
        return Result.success(gameService.getStats(userId));
    }

    /** 在线匹配：有空房则加入开局 */
    @PostMapping("/match")
    public Result<GameMatchVO> matchGame(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody GameJoinDTO dto) {
        return Result.success(gameService.matchGame(userId, dto));
    }

    /** 创建对局或等待房间（FR4.1），返回 gameId */
    @PostMapping
    public Result<Long> createGame(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody GameCreateDTO dto) {
        return Result.success(gameService.createGame(userId, dto));
    }

    /** 加入等待中的房间 */
    @PostMapping("/{id}/join")
    public Result<Long> joinGame(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody GameJoinDTO dto) {
        return Result.success(gameService.joinGame(userId, id, dto));
    }

    /** 取消本人发起的等待房间 */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelWaiting(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable Long id) {
        gameService.cancelWaiting(userId, id);
        return Result.success();
    }

    /** 查询对局状态（FR4.2） */
    @GetMapping("/{id}")
    public Result<GameStateVO> getGameState(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable Long id) {
        return Result.success(gameService.getGameState(userId, id));
    }

    /** 执行操作（FR4.3） */
    @PostMapping("/{id}/actions")
    public Result<ActionResultVO> executeAction(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody ActionRequestDTO dto) {
        return Result.success(gameService.executeAction(userId, id, dto));
    }

    /** 认输（FR4.4） */
    @PostMapping("/{id}/surrender")
    public Result<Void> surrender(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable Long id) {
        gameService.surrender(userId, id);
        return Result.success();
    }

    /** 复盘数据（FR4.5） */
    @GetMapping("/{id}/replay")
    public Result<ReplayVO> getReplay(@PathVariable Long id) {
        return Result.success(gameService.getReplay(id));
    }
}
