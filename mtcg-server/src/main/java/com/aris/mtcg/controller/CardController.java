package com.aris.mtcg.controller;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.CardCreateDTO;
import com.aris.mtcg.domain.dto.CardQueryDTO;
import com.aris.mtcg.domain.dto.CardUpdateDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡牌管理接口
 *
 * @author pengYuJun
 */
@Tag(name = "卡牌管理")
@RestController
@RequestMapping("/admin/cards")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class CardController {

    @Resource
    private CardService cardService;

    @Operation(summary = "分页查询卡牌列表")
    @GetMapping
    public Result<PageVO<CardVO>> list(CardQueryDTO query) {
        return Result.success(cardService.listCards(query));
    }

    @Operation(summary = "根据ID查询卡牌")
    @GetMapping("/{id}")
    public Result<CardVO> get(@PathVariable Long id) {
        return Result.success(cardService.getCardById(id));
    }

    @Operation(summary = "新增卡牌")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CardCreateDTO dto) {
        return Result.success(cardService.createCard(dto));
    }

    @Operation(summary = "更新卡牌")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CardUpdateDTO dto) {
        cardService.updateCard(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除卡牌")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        cardService.deleteCard(id);
        return Result.success();
    }
}
