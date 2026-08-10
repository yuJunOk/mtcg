package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardFeatureMapper;
import com.aris.mtcg.dao.CardFeatureRelMapper;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.domain.dto.CardFeatureCreateDTO;
import com.aris.mtcg.domain.dto.CardFeatureUpdateDTO;
import com.aris.mtcg.domain.entity.CardFeatureDO;
import com.aris.mtcg.domain.entity.CardFeatureRelDO;
import com.aris.mtcg.domain.vo.CardFeatureVO;
import com.aris.mtcg.service.AuditService;
import com.aris.mtcg.service.CardFeatureService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 卡牌特征服务实现
 *
 * @author pengYuJun
 */
@Service
public class CardFeatureServiceImpl implements CardFeatureService {

    @Resource private CardFeatureMapper cardFeatureMapper;

    @Resource private CardFeatureRelMapper cardFeatureRelMapper;

    @Resource private CardMapper cardMapper;

    @Resource private AuditService auditService;

    @Override
    public List<CardFeatureVO> listAll() {
        return cardFeatureMapper.selectAll().stream()
                .map(CardFeatureVO::fromDO)
                .collect(Collectors.toList());
    }

    @Override
    public CardFeatureVO getById(Long id) {
        return CardFeatureVO.fromDO(loadOrThrow(id));
    }

    @Override
    public Long create(CardFeatureCreateDTO dto) {
        long count =
                cardFeatureMapper.selectCountByQuery(
                        QueryWrapper.create().eq("code", dto.getCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CARD_FEATURE_CODE_DUPLICATE);
        }
        CardFeatureDO entity = CardFeatureVO.toDO(CardFeatureVO.fromDTO(dto));
        cardFeatureMapper.insert(entity);
        auditService.record(
                "CREATE",
                "CARD_FEATURE",
                String.valueOf(entity.getId()),
                "创建特征 " + entity.getCode());
        return entity.getId();
    }

    @Override
    public void update(Long id, CardFeatureUpdateDTO dto) {
        loadOrThrow(id);
        CardFeatureDO update = new CardFeatureDO();
        update.setId(id);
        if (dto.getName() != null) {
            update.setName(dto.getName());
        }
        if (dto.getBgColor() != null) {
            update.setBgColor(dto.getBgColor());
        }
        cardFeatureMapper.update(update);
        auditService.record("UPDATE", "CARD_FEATURE", String.valueOf(id), "更新特征");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        CardFeatureDO entity = loadOrThrow(id);
        cardFeatureRelMapper.deleteByQuery(QueryWrapper.create().eq("feature_id", id));
        cardFeatureMapper.deleteById(id);
        auditService.record(
                "DELETE", "CARD_FEATURE", String.valueOf(id), "删除特征 " + entity.getCode());
    }

    @Override
    public List<CardFeatureVO> listByCardId(Long cardId) {
        ensureCardExists(cardId);
        List<CardFeatureRelDO> rels =
                cardFeatureRelMapper.selectListByQuery(QueryWrapper.create().eq("card_id", cardId));
        if (rels.isEmpty()) {
            return List.of();
        }
        List<Long> featureIds = rels.stream().map(CardFeatureRelDO::getFeatureId).toList();
        return cardFeatureMapper.selectListByIds(featureIds).stream()
                .map(CardFeatureVO::fromDO)
                .collect(Collectors.toList());
    }

    @Override
    public void addToCard(Long cardId, Long featureId) {
        ensureCardExists(cardId);
        loadOrThrow(featureId);
        long count =
                cardFeatureRelMapper.selectCountByQuery(
                        QueryWrapper.create().eq("card_id", cardId).eq("feature_id", featureId));
        if (count > 0) {
            return;
        }
        CardFeatureRelDO rel = new CardFeatureRelDO();
        rel.setCardId(cardId);
        rel.setFeatureId(featureId);
        cardFeatureRelMapper.insert(rel);
        auditService.record("UPDATE", "CARD", String.valueOf(cardId), "关联特征 " + featureId);
    }

    @Override
    public void removeFromCard(Long cardId, Long featureId) {
        ensureCardExists(cardId);
        loadOrThrow(featureId);
        cardFeatureRelMapper.deleteByQuery(
                QueryWrapper.create().eq("card_id", cardId).eq("feature_id", featureId));
        auditService.record("UPDATE", "CARD", String.valueOf(cardId), "移除特征 " + featureId);
    }

    private void ensureCardExists(Long cardId) {
        if (cardMapper.selectOneById(cardId) == null) {
            throw new BusinessException(ErrorCode.CARD_NOT_FOUND);
        }
    }

    private CardFeatureDO loadOrThrow(Long id) {
        CardFeatureDO entity = cardFeatureMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.CARD_FEATURE_NOT_FOUND);
        }
        return entity;
    }
}
