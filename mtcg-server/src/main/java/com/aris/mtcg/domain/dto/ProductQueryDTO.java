package com.aris.mtcg.domain.dto;

import lombok.Data;

/**
 * 产品分页查询入参
 *
 * @author pengYuJun
 */
@Data
public class ProductQueryDTO {

    /** 产品名称（模糊匹配） */
    private String productName;

    /** 产品编号（模糊匹配） */
    private String productCode;

    /** 当前页码，默认 1 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}
