package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.CardCollectionDTO;
import com.aris.mtcg.domain.vo.CardCollectionVO;
import java.util.List;

/**
 * 收藏服务接口
 *
 * @author pengYuJun
 */
public interface CardCollectionService {

    /** 查询收藏列表 */
    List<CardCollectionVO> listCollection(Long userId);

    /** 查询收藏列表（按标签筛选） */
    List<CardCollectionVO> listCollection(Long userId, String tag);

    /** 获取单卡收藏 */
    CardCollectionVO getCollection(Long userId, String cardCode);

    /** 登记收藏（累加数量） */
    void addCollection(Long userId, CardCollectionDTO dto);

    /** 设置数量（覆盖） */
    void setCollection(Long userId, CardCollectionDTO dto);

    /** 移除收藏 */
    void removeCollection(Long userId, String cardCode);
}
