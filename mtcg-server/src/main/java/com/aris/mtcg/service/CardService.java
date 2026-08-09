package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.CardCreateDTO;
import com.aris.mtcg.domain.dto.CardQueryDTO;
import com.aris.mtcg.domain.dto.CardUpdateDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;

/**
 * 卡牌服务
 *
 * @author pengYuJun
 */
public interface CardService {

    /**
     * 分页查询卡牌列表
     */
    PageVO<CardVO> listCards(CardQueryDTO query);

    /**
     * 根据 ID 查询卡牌
     */
    CardVO getCardById(Long id);

    /**
     * 创建卡牌
     */
    Long createCard(CardCreateDTO dto);

    /**
     * 更新卡牌
     */
    void updateCard(Long id, CardUpdateDTO dto);

    /**
     * 删除卡牌
     */
    void deleteCard(Long id);
}
