package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.enums.EnumCardType;
import com.aris.mtcg.common.enums.EnumColor;
import com.aris.mtcg.common.enums.EnumRarity;
import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.domain.dto.CardCreateDTO;
import com.aris.mtcg.domain.dto.CardQueryDTO;
import com.aris.mtcg.domain.dto.CardUpdateDTO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.service.CardService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌服务实现
 *
 * @author pengYuJun
 */
@Service
public class CardServiceImpl implements CardService {

    @Resource
    private CardMapper cardMapper;

    @Override
    public PageVO<CardVO> listCards(CardQueryDTO query) {
        QueryWrapper qw = QueryWrapper.create()
                .like("card_name", query.getCardName(), StringUtils::isNotBlank)
                .eq("card_type", query.getCardType(), StringUtils::isNotBlank)
                .eq("color", query.getColor(), StringUtils::isNotBlank)
                .eq("rarity", query.getRarity(), StringUtils::isNotBlank)
                .eq("product_code", query.getProductCode(), StringUtils::isNotBlank)
                .orderBy("create_time", false);
        int pageNum = (query.getPage() == null || query.getPage() < 1) ? 1 : query.getPage();
        int pageSize = (query.getSize() == null || query.getSize() < 1) ? 20 : query.getSize();
        Page<CardDO> page = cardMapper.paginate(Page.of(pageNum, pageSize), qw);
        List<CardVO> records = page.getRecords().stream()
                .map(CardVO::fromDO)
                .collect(Collectors.toList());
        return new PageVO<>(records, page.getTotalRow());
    }

    @Override
    public CardVO getCardById(Long id) {
        return CardVO.fromDO(loadOrThrow(id));
    }

    @Override
    public Long createCard(CardCreateDTO dto) {
        // 校验编号唯一
        long count = cardMapper.selectCountByQuery(
                QueryWrapper.create().eq("card_code", dto.getCardCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CARD_CODE_DUPLICATE);
        }
        // 校验枚举合法性
        validateCreateEnums(dto);
        CardDO card = CardVO.toDO(CardVO.fromDTO(dto));
        cardMapper.insert(card);
        return card.getId();
    }

    @Override
    public void updateCard(Long id, CardUpdateDTO dto) {
        loadOrThrow(id);
        // 校验更新的枚举合法性（仅对非空字段）
        if (dto.getCardType() != null && EnumCardType.of(dto.getCardType()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法卡牌类型");
        }
        if (dto.getColor() != null && EnumColor.of(dto.getColor()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法颜色");
        }
        if (dto.getRarity() != null && EnumRarity.of(dto.getRarity()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法稀有度");
        }
        CardDO update = new CardDO();
        update.setId(id);
        if (dto.getProductCode() != null) {
            update.setProductCode(dto.getProductCode());
        }
        if (dto.getCardName() != null) {
            update.setCardName(dto.getCardName());
        }
        if (dto.getCardType() != null) {
            update.setCardType(dto.getCardType());
        }
        if (dto.getLevel() != null) {
            update.setLevel(dto.getLevel());
        }
        if (dto.getColor() != null) {
            update.setColor(dto.getColor());
        }
        if (dto.getEnvironment() != null) {
            update.setEnvironment(dto.getEnvironment());
        }
        if (dto.getTraits() != null) {
            update.setTraits(dto.getTraits());
        }
        if (dto.getAttackRange() != null) {
            update.setAttackRange(dto.getAttackRange());
        }
        if (dto.getPower() != null) {
            update.setPower(dto.getPower());
        }
        if (dto.getRarity() != null) {
            update.setRarity(dto.getRarity());
        }
        if (dto.getEffectText() != null) {
            update.setEffectText(dto.getEffectText());
        }
        if (dto.getEffectJson() != null) {
            update.setEffectJson(dto.getEffectJson());
        }
        if (dto.getImagePath() != null) {
            update.setImagePath(dto.getImagePath());
        }
        cardMapper.update(update);
    }

    @Override
    public void deleteCard(Long id) {
        loadOrThrow(id);
        cardMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    private void validateCreateEnums(CardCreateDTO dto) {
        if (EnumCardType.of(dto.getCardType()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法卡牌类型");
        }
        if (dto.getColor() != null && EnumColor.of(dto.getColor()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法颜色");
        }
        if (EnumRarity.of(dto.getRarity()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法稀有度");
        }
        if (dto.getLevel() != null && (dto.getLevel() < 1 || dto.getLevel() > 6)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级必须在 1-6 之间");
        }
    }

    private CardDO loadOrThrow(Long id) {
        CardDO card = cardMapper.selectOneById(id);
        if (card == null) {
            throw new BusinessException(ErrorCode.CARD_NOT_FOUND);
        }
        return card;
    }
}
