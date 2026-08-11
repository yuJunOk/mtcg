package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.CardCollectionDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏 Mapper
 *
 * @author pengYuJun
 */
@Mapper
public interface CardCollectionMapper extends BaseMapper<CardCollectionDO> {

    /** upsert 累加：存在则数量累加，不存在则插入 */
    @Insert(
            """
            INSERT INTO mtcg_card_collection(user_id, card_code, quantity, tags, note, create_time, update_time)
            VALUES(#{userId}, #{cardCode}, #{quantity}, #{tags}, #{note}, NOW(), NOW())
            ON CONFLICT (user_id, card_code)
            DO UPDATE SET quantity = mtcg_card_collection.quantity + #{quantity},
                          tags = COALESCE(NULLIF(#{tags}, ''), mtcg_card_collection.tags),
                          note = COALESCE(NULLIF(#{note}, ''), mtcg_card_collection.note),
                          update_time = NOW()
            """)
    int upsertAdd(
            @Param("userId") Long userId,
            @Param("cardCode") String cardCode,
            @Param("quantity") Integer quantity,
            @Param("tags") String tags,
            @Param("note") String note);

    /** upsert 设置：存在则覆盖数量，不存在则插入 */
    @Insert(
            """
            INSERT INTO mtcg_card_collection(user_id, card_code, quantity, tags, note, create_time, update_time)
            VALUES(#{userId}, #{cardCode}, #{quantity}, #{tags}, #{note}, NOW(), NOW())
            ON CONFLICT (user_id, card_code)
            DO UPDATE SET quantity = #{quantity},
                          tags = COALESCE(NULLIF(#{tags}, ''), mtcg_card_collection.tags),
                          note = COALESCE(NULLIF(#{note}, ''), mtcg_card_collection.note),
                          update_time = NOW()
            """)
    int upsertSet(
            @Param("userId") Long userId,
            @Param("cardCode") String cardCode,
            @Param("quantity") Integer quantity,
            @Param("tags") String tags,
            @Param("note") String note);
}
