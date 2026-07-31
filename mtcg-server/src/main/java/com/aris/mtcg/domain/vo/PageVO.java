package com.aris.mtcg.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页展示对象
 *
 * @param <T> 记录类型
 * @author pengYuJun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {

    /**
     * 当前页记录
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;
}
