package com.aris.mtcg.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * 场上区域视图（公开）
 *
 * @author pengYuJun
 */
@Data
public class FieldZoneVO {

    private CardInstanceVO vanguard;

    /** 侧翼，长度 2，空位以 null 占位 */
    private List<CardInstanceVO> flank;

    private CardInstanceVO rearguard;

    /** 基地区，长度 ≤ 6 */
    private List<CardInstanceVO> base;
}
