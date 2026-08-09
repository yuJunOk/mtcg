package com.aris.mtcg.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 产品更新入参（所有字段可空，非空才更新）
 *
 * @author pengYuJun
 */
@Data
public class ProductUpdateDTO {

    @Length(max = 128)
    private String productName;

    private LocalDate releaseDate;

    private String description;
}
