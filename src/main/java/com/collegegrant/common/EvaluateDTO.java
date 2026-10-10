package com.collegegrant.common;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class EvaluateDTO {
    @NotNull(message = "助学金类型ID不能为空")
    private Long typeId;

    @NotNull(message = "家庭经济权重不能为空")
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 100, message = "权重不能大于100")
    private Integer economicWeight;

    @NotNull(message = "家庭负担权重不能为空")
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 100, message = "权重不能大于100")
    private Integer familyBurdenWeight;

    @NotNull(message = "特殊困难权重不能为空")
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 100, message = "权重不能大于100")
    private Integer specialHardshipWeight;

    @NotNull(message = "突发困难权重不能为空")
    @Min(value = 0, message = "权重不能小于0")
    @Max(value = 100, message = "权重不能大于100")
    private Integer disasterWeight;
}
