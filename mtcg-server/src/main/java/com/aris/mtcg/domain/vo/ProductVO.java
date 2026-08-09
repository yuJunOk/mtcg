package com.aris.mtcg.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 产品展示对象
 *
 * @author pengYuJun
 */
@Data
public class ProductVO {

    private Long id;

    private String productCode;

    private String productName;

    private LocalDate releaseDate;

    private String description;

    private LocalDateTime createTime;
}
