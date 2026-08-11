package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardCollectionMapper;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.domain.dto.CardCollectionDTO;
import com.aris.mtcg.domain.entity.CardCollectionDO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.vo.CardCollectionVO;
import com.aris.mtcg.service.CardCollectionService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 收藏服务实现
 *
 * @author pengYuJun
 */
@Service
public class CardCollectionServiceImpl implements CardCollectionService {

    @Resource private CardCollectionMapper collectionMapper;

    @Resource private CardMapper cardMapper;

    @Override
    public List<CardCollectionVO> listCollection(Long userId) {
        return listCollection(userId, null);
    }

    @Override
    public List<CardCollectionVO> listCollection(Long userId, String tag) {
        QueryWrapper qw = QueryWrapper.create().eq("user_id", userId);
        if (StringUtils.isNotBlank(tag)) {
            // 边界匹配：与卡组标签筛选约定一致
            qw.and("(',' || COALESCE(tags, '') || ',') LIKE {0}", "%," + tag.trim() + ",%");
        }
        qw.orderBy("update_time", false);
        List<CardCollectionDO> collections = collectionMapper.selectListByQuery(qw);

        List<String> codes = collections.stream().map(CardCollectionDO::getCardCode).toList();
        Map<String, String> code2Name = resolveCardNames(codes);

        return collections.stream()
                .map(
                        c -> {
                            CardCollectionVO vo = CardCollectionVO.fromDO(c);
                            vo.setCardName(code2Name.get(c.getCardCode()));
                            return vo;
                        })
                .collect(Collectors.toList());
    }

    @Override
    public CardCollectionVO getCollection(Long userId, String cardCode) {
        CardCollectionDO collection = findByUserAndCode(userId, cardCode);
        if (collection == null) {
            throw new BusinessException(ErrorCode.COLLECTION_NOT_FOUND);
        }
        CardCollectionVO vo = CardCollectionVO.fromDO(collection);
        vo.setCardName(getCardName(cardCode));
        return vo;
    }

    @Override
    public void addCollection(Long userId, CardCollectionDTO dto) {
        validateCardCode(dto.getCardCode());
        collectionMapper.upsertAdd(
                userId, dto.getCardCode(), dto.getQuantity(), dto.getTags(), dto.getNote());
    }

    @Override
    public void setCollection(Long userId, CardCollectionDTO dto) {
        validateCardCode(dto.getCardCode());
        // quantity = 0 时删除
        if (dto.getQuantity() != null && dto.getQuantity() <= 0) {
            removeCollection(userId, dto.getCardCode());
            return;
        }
        collectionMapper.upsertSet(
                userId, dto.getCardCode(), dto.getQuantity(), dto.getTags(), dto.getNote());
    }

    @Override
    public void removeCollection(Long userId, String cardCode) {
        CardCollectionDO collection = findByUserAndCode(userId, cardCode);
        if (collection != null) {
            collectionMapper.deleteById(collection.getId());
        }
    }

    // ========== 私有方法 ==========

    private CardCollectionDO findByUserAndCode(Long userId, String cardCode) {
        return collectionMapper.selectOneByQuery(
                QueryWrapper.create().eq("user_id", userId).eq("card_code", cardCode));
    }

    private void validateCardCode(String cardCode) {
        if (cardCode == null || cardCode.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "卡牌编号不能为空");
        }
        long count = cardMapper.selectCountByQuery(QueryWrapper.create().eq("card_code", cardCode));
        if (count == 0) {
            throw new BusinessException(ErrorCode.CARD_CODE_NOT_EXIST);
        }
    }

    private String getCardName(String cardCode) {
        CardDO card = cardMapper.selectOneByQuery(QueryWrapper.create().eq("card_code", cardCode));
        return card == null ? null : card.getCardName();
    }

    private Map<String, String> resolveCardNames(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyMap();
        }
        return cardMapper.selectListByQuery(QueryWrapper.create().in("card_code", codes)).stream()
                .collect(Collectors.toMap(CardDO::getCardCode, CardDO::getCardName, (a, b) -> a));
    }
}
