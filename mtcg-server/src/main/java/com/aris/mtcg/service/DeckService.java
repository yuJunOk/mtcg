package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.DeckCardEntry;
import com.aris.mtcg.domain.dto.DeckCreateDTO;
import com.aris.mtcg.domain.dto.DeckReorderDTO;
import com.aris.mtcg.domain.dto.DeckUpdateDTO;
import com.aris.mtcg.domain.vo.DeckVO;
import com.aris.mtcg.domain.vo.DeckValidateResultVO;
import java.util.List;

/**
 * 卡组服务接口
 *
 * @author pengYuJun
 */
public interface DeckService {

    /** 创建卡组 */
    Long createDeck(Long userId, DeckCreateDTO dto);

    /** 编辑卡组 */
    void updateDeck(Long userId, Long deckId, DeckUpdateDTO dto);

    /** 删除卡组 */
    void deleteDeck(Long userId, Long deckId);

    /** 获取卡组详情 */
    DeckVO getDeck(Long userId, Long deckId);

    /** 查询我的卡组列表（按 sort_order；tag 非空时精确包含筛选） */
    List<DeckVO> listDecks(Long userId, String tag);

    /** 批量重排卡组 */
    void reorderDecks(Long userId, DeckReorderDTO dto);

    /** 校验卡组合法性（实时校验） */
    DeckValidateResultVO validateDeck(Long userId, Long deckId);

    /** 校验卡牌条目列表（张数按 sum(quantity)） */
    DeckValidateResultVO validateEntries(
            List<DeckCardEntry> mainDeck, List<DeckCardEntry> rushDeck);
}
