package com.aris.mtcg.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardFeatureMapper;
import com.aris.mtcg.dao.CardFeatureRelMapper;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.entity.CardFeatureDO;
import com.aris.mtcg.service.AuditService;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** 卡牌特征关联单元测试 */
@ExtendWith(MockitoExtension.class)
class CardFeatureServiceImplTest {

    @Mock private CardFeatureMapper cardFeatureMapper;

    @Mock private CardFeatureRelMapper cardFeatureRelMapper;

    @Mock private CardMapper cardMapper;

    @Mock private AuditService auditService;

    @InjectMocks private CardFeatureServiceImpl cardFeatureService;

    private CardDO card;
    private CardFeatureDO feature;

    @BeforeEach
    void setUp() {
        card = new CardDO();
        card.setId(10L);
        card.setCardCode("HR-001");
        feature = new CardFeatureDO();
        feature.setId(20L);
        feature.setCode("FLY");
    }

    @Test
    void addToCard_shouldInsertWhenNotExists() {
        when(cardMapper.selectOneById(10L)).thenReturn(card);
        when(cardFeatureMapper.selectOneById(20L)).thenReturn(feature);
        when(cardFeatureRelMapper.selectCountByQuery(any(QueryWrapper.class))).thenReturn(0L);

        cardFeatureService.addToCard(10L, 20L);

        ArgumentCaptor<com.aris.mtcg.domain.entity.CardFeatureRelDO> captor =
                ArgumentCaptor.forClass(com.aris.mtcg.domain.entity.CardFeatureRelDO.class);
        verify(cardFeatureRelMapper).insert(captor.capture());
        assertEquals(10L, captor.getValue().getCardId());
        assertEquals(20L, captor.getValue().getFeatureId());
        verify(auditService).record(any(), any(), any(), any());
    }

    @Test
    void addToCard_shouldSkipWhenAlreadyAssociated() {
        when(cardMapper.selectOneById(10L)).thenReturn(card);
        when(cardFeatureMapper.selectOneById(20L)).thenReturn(feature);
        when(cardFeatureRelMapper.selectCountByQuery(any(QueryWrapper.class))).thenReturn(1L);

        cardFeatureService.addToCard(10L, 20L);

        verify(cardFeatureRelMapper, never()).insert(any());
    }

    @Test
    void addToCard_shouldFailWhenCardMissing() {
        when(cardMapper.selectOneById(10L)).thenReturn(null);
        BusinessException ex =
                assertThrows(BusinessException.class, () -> cardFeatureService.addToCard(10L, 20L));
        assertEquals(ErrorCode.CARD_NOT_FOUND, ex.getErrorCode());
    }
}
