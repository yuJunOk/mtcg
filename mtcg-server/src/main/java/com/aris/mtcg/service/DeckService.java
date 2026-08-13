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

    /** 创建卡组，返回对外业务编码 deckCode */
    String createDeck(Long userId, DeckCreateDTO dto);

    /** 编辑卡组（id 可为数字主键或 D- 编码） */
    void updateDeck(Long userId, String idOrCode, DeckUpdateDTO dto);

    /** 删除卡组（id 可为数字主键或 D- 编码） */
    void deleteDeck(Long userId, String idOrCode);

    /** 获取卡组详情（id 可为数字主键或 D- 编码） */
    DeckVO getDeck(Long userId, String idOrCode);

    /** 查询我的卡组列表（按 sort_order；tag 非空时精确包含筛选） */
    List<DeckVO> listDecks(Long userId, String tag);

    /** 批量重排卡组 */
    void reorderDecks(Long userId, DeckReorderDTO dto);

    /** 校验卡组合法性（实时校验；id 可为数字主键或 D- 编码） */
    DeckValidateResultVO validateDeck(Long userId, String idOrCode);

    /** 校验卡牌条目列表（张数按 sum(quantity)） */
    DeckValidateResultVO validateEntries(
            List<DeckCardEntry> mainDeck, List<DeckCardEntry> rushDeck);

    /**
     * 按卡组编码复制到当前用户（源卡组须允许复制，或属于本人）。
     *
     * @return 新卡组 deckCode
     */
    String copyDeckByCode(Long userId, String deckCode);
}
