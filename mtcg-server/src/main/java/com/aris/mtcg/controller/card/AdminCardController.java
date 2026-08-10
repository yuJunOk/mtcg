package com.aris.mtcg.controller.card;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.CardCreateDTO;
import com.aris.mtcg.domain.dto.CardUpdateDTO;
import com.aris.mtcg.domain.vo.CardFeatureVO;
import com.aris.mtcg.service.CardFeatureService;
import com.aris.mtcg.service.CardService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员卡牌管理接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/admin/cards")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class AdminCardController {

    @Resource private CardService cardService;

    @Resource private CardFeatureService cardFeatureService;

    /** 新增卡牌 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CardCreateDTO dto) {
        return Result.success(cardService.createCard(dto));
    }

    /** 更新卡牌 */
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CardUpdateDTO dto) {
        cardService.updateCard(id, dto);
        return Result.success();
    }

    /** 删除卡牌 */
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        cardService.deleteCard(id);
        return Result.success();
    }

    /** 上传卡牌图片 */
    @PostMapping("/{id}/image")
    public Result<String> uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return Result.success(cardService.uploadCardImage(id, file));
    }

    /** 查询卡牌关联特征 */
    @GetMapping("/{id}/features")
    public Result<List<CardFeatureVO>> listFeatures(@PathVariable Long id) {
        return Result.success(cardFeatureService.listByCardId(id));
    }

    /** 为卡牌添加特征 */
    @PostMapping("/{id}/features/{featureId}")
    public Result<Void> addFeature(@PathVariable Long id, @PathVariable Long featureId) {
        cardFeatureService.addToCard(id, featureId);
        return Result.success();
    }

    /** 移除卡牌特征 */
    @PostMapping("/{id}/features/{featureId}/delete")
    public Result<Void> removeFeature(@PathVariable Long id, @PathVariable Long featureId) {
        cardFeatureService.removeFromCard(id, featureId);
        return Result.success();
    }
}
