package com.aris.mtcg.domain.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * 卡组校验结果视图
 *
 * @author pengYuJun
 */
@Data
public class DeckValidateResultVO {

    /** 是否合法 */
    private Boolean valid;

    /** 不合法原因列表 */
    private List<String> errors = new ArrayList<>();

    /** 主卡组实际张数 */
    private Integer mainDeckCount;

    /** 冲击卡组实际张数 */
    private Integer rushDeckCount;

    /** 主卡组涉及的颜色 */
    private List<String> colors = new ArrayList<>();

    /** 同名卡牌统计（卡名 → 张数） */
    private Map<String, Integer> nameCount = new HashMap<>();
}
