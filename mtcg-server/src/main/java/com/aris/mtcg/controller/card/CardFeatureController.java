package com.aris.mtcg.controller.card;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.CardFeatureCreateDTO;
import com.aris.mtcg.domain.dto.CardFeatureUpdateDTO;
import com.aris.mtcg.domain.vo.CardFeatureVO;
import com.aris.mtcg.service.CardFeatureService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡牌特征管理接口（管理员）
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/cards/features")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class CardFeatureController {

    @Resource private CardFeatureService cardFeatureService;

    /** 获取所有特征 */
    @GetMapping
    public Result<List<CardFeatureVO>> listAll() {
        return Result.success(cardFeatureService.listAll());
    }

    /** 获取特征详情 */
    @GetMapping("/{id}")
    public Result<CardFeatureVO> get(@PathVariable Long id) {
        return Result.success(cardFeatureService.getById(id));
    }

    /** 创建特征 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CardFeatureCreateDTO dto) {
        return Result.success(cardFeatureService.create(dto));
    }

    /** 更新特征 */
    @PostMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id, @Valid @RequestBody CardFeatureUpdateDTO dto) {
        cardFeatureService.update(id, dto);
        return Result.success();
    }

    /** 删除特征 */
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        cardFeatureService.delete(id);
        return Result.success();
    }
}
