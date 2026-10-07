package com.collegegrant.common;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GrantTypeWithLevelVO {
    /** 助学金类型ID */
    private Long id;
    /** 助学金名称 */
    private String typeName;
    /** 描述说明 */
    private String description;
    /** 等级列表 */
    private List<GrantLevelVO> levels;

    @Data
    public static class GrantLevelVO {
        /** 等级ID */
        private Long id;
        /** 等级名称 */
        private String levelName;
        /** 金额（元） */
        private BigDecimal amount;
        /** 最低分数线 */
        private BigDecimal minScore;
        /** 排序号 */
        private Integer sort;
    }
}