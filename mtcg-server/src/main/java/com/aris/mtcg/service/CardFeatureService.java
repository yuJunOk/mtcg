package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.CardFeatureCreateDTO;
import com.aris.mtcg.domain.dto.CardFeatureUpdateDTO;
import com.aris.mtcg.domain.vo.CardFeatureVO;
import java.util.List;

/**
 * 卡牌特征服务接口
 *
 * @author pengYuJun
 */
public interface CardFeatureService {

    /** 获取所有特征 */
    List<CardFeatureVO> listAll();

    /** 根据 ID 获取特征 */
    CardFeatureVO getById(Long id);

    /** 创建特征 */
    Long create(CardFeatureCreateDTO dto);

    /** 更新特征 */
    void update(Long id, CardFeatureUpdateDTO dto);

    /** 删除特征 */
    void delete(Long id);

    /** 根据卡牌 ID 获取特征列表 */
    List<CardFeatureVO> listByCardId(Long cardId);

    /** 为卡牌添加特征 */
    void addToCard(Long cardId, Long featureId);

    /** 为卡牌移除特征 */
    void removeFromCard(Long cardId, Long featureId);
}
