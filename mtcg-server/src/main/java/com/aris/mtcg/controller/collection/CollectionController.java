package com.aris.mtcg.controller.collection;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.CardCollectionDTO;
import com.aris.mtcg.domain.vo.CardCollectionVO;
import com.aris.mtcg.service.CardCollectionService;
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
 * 收藏 REST API
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/collections")
public class CollectionController {

    @Resource private CardCollectionService collectionService;

    /** 查询收藏列表（tag 非空时精确包含筛选） */
    @GetMapping
    public Result<List<CardCollectionVO>> listCollection(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @RequestParam(required = false) String tag) {
        return Result.success(collectionService.listCollection(userId, tag));
    }

    /** 获取单卡收藏 */
    @GetMapping("/{cardCode}")
    public Result<CardCollectionVO> getCollection(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable String cardCode) {
        return Result.success(collectionService.getCollection(userId, cardCode));
    }

    /** 登记收藏（累加数量） */
    @PostMapping
    public Result<Void> addCollection(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody CardCollectionDTO dto) {
        collectionService.addCollection(userId, dto);
        return Result.success();
    }

    /** 设置收藏数量（覆盖） */
    @PostMapping("/{cardCode}")
    public Result<Void> setCollection(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable String cardCode,
            @Valid @RequestBody CardCollectionDTO dto) {
        dto.setCardCode(cardCode);
        collectionService.setCollection(userId, dto);
        return Result.success();
    }

    /** 移除收藏 */
    @PostMapping("/{cardCode}/delete")
    public Result<Void> removeCollection(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @PathVariable String cardCode) {
        collectionService.removeCollection(userId, cardCode);
        return Result.success();
    }
}
