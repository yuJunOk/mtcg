package com.aris.mtcg.controller.card;

import com.aris.mtcg.common.annotation.PublicApi;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.CardQueryDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.service.CardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开卡牌接口（无需登录）
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/cards")
@PublicApi
public class CardController {

    @Resource private CardService cardService;

    /** 分页查询卡牌列表 */
    @GetMapping
    public Result<PageVO<CardVO>> list(CardQueryDTO query) {
        return Result.success(cardService.listCards(query));
    }

    /** 查询卡牌详情 */
    @GetMapping("/{id}")
    public Result<CardVO> get(@PathVariable Long id) {
        return Result.success(cardService.getCardById(id));
    }
}
