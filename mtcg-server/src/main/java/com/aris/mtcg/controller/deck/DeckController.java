package com.aris.mtcg.controller.deck;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.DeckCopyDTO;
import com.aris.mtcg.domain.dto.DeckCreateDTO;
import com.aris.mtcg.domain.dto.DeckReorderDTO;
import com.aris.mtcg.domain.dto.DeckUpdateDTO;
import com.aris.mtcg.domain.vo.DeckVO;
import com.aris.mtcg.domain.vo.DeckValidateResultVO;
import com.aris.mtcg.service.DeckService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡组 REST API
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/decks")
public class DeckController {

    @Resource private DeckService deckService;

    /** 查询我的卡组列表（按 sort_order；tag 非空时精确包含筛选） */
    @GetMapping
    public Result<List<DeckVO>> listDecks(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @RequestParam(required = false) String tag) {
        return Result.success(deckService.listDecks(userId, tag));
    }

    /** 创建卡组，返回对外业务编码 deckCode */
    @PostMapping
    public Result<String> createDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody DeckCreateDTO dto) {
        return Result.success(deckService.createDeck(userId, dto));
    }

    /** 批量重排卡组列表 */
    @PostMapping("/reorder")
    public Result<Void> reorderDecks(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody DeckReorderDTO dto) {
        deckService.reorderDecks(userId, dto);
        return Result.success();
    }

    /** 按编码复制卡组（源卡组须开启可复制，或属于本人） */
    @PostMapping("/copy")
    public Result<String> copyDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody DeckCopyDTO dto) {
        return Result.success(deckService.copyDeckByCode(userId, dto.getDeckCode()));
    }

    /** 获取卡组详情（id 可为数字主键或 D- 编码） */
    @GetMapping("/{id}")
    public Result<DeckVO> getDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable String id) {
        return Result.success(deckService.getDeck(userId, id));
    }

    /** 编辑卡组（id 可为数字主键或 D- 编码） */
    @PostMapping("/{id}")
    public Result<Void> updateDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable String id,
            @Valid @RequestBody DeckUpdateDTO dto) {
        deckService.updateDeck(userId, id, dto);
        return Result.success();
    }

    /** 删除卡组（id 可为数字主键或 D- 编码） */
    @PostMapping("/{id}/delete")
    public Result<Void> deleteDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable String id) {
        deckService.deleteDeck(userId, id);
        return Result.success();
    }

    /** 校验卡组合法性（id 可为数字主键或 D- 编码） */
    @PostMapping("/{id}/validate")
    public Result<DeckValidateResultVO> validateDeck(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId, @PathVariable String id) {
        return Result.success(deckService.validateDeck(userId, id));
    }
}
