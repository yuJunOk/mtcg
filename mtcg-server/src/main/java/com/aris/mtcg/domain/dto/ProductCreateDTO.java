package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 产品创建入参
 *
 * @author pengYuJun
 */
@Data
public class ProductCreateDTO {

    @NotBlank(message = "产品编号不能为空")
    @Length(max = 16, message = "产品编号长度不能超过 16")
    private String productCode;

    @NotBlank(message = "产品名称不能为空")
    @Length(max = 128, message = "产品名称长度不能超过 128")
    private String productName;

    private LocalDate releaseDate;

    private String description;
}
