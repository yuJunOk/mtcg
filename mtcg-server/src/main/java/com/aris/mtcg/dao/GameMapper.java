package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.GameDO;
import com.mybatisflex.core.BaseMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 对局记录 Mapper
 *
 * @author pengYuJun
 */
@Mapper
public interface GameMapper extends BaseMapper<GameDO> {

    /** 个人对局历史（FR5.4）：参与方为本人，按创建时间倒序分页 */
    @Select(
            """
            SELECT * FROM mtcg_game_record
            WHERE player1_id = #{userId} OR player2_id = #{userId}
            ORDER BY create_time DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<GameDO> selectHistory(
            @Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 胜败统计（FR5.4）：仅已结束对局；按本人所在方与 winner 计算
     *
     * <p>返回键：total / wins / losses / draws
     */
    @Select(
            """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN (player1_id = #{userId} AND winner = 'PLAYER1')
                          OR (player2_id = #{userId} AND winner = 'PLAYER2')
                     THEN 1 ELSE 0 END) AS wins,
                SUM(CASE WHEN (player1_id = #{userId} AND winner = 'PLAYER2')
                          OR (player2_id = #{userId} AND winner = 'PLAYER1')
                     THEN 1 ELSE 0 END) AS losses,
                SUM(CASE WHEN winner = 'DRAW' THEN 1 ELSE 0 END) AS draws
            FROM mtcg_game_record
            WHERE (player1_id = #{userId} OR player2_id = #{userId}) AND status = 'FINISHED'
            """)
    Map<String, Object> selectStats(@Param("userId") Long userId);

    /** 个人对局历史总数（分页用） */
    @Select(
            """
            SELECT COUNT(*) FROM mtcg_game_record
            WHERE player1_id = #{userId} OR player2_id = #{userId}
            """)
    long countHistory(@Param("userId") Long userId);
}
