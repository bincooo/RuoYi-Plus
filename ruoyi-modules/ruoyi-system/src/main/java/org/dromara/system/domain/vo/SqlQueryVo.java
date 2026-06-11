package org.dromara.system.domain.vo;

import lombok.Data;

@Data
public class SqlQueryVo {
    /** 主键 */
    private String id;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 描述 */
    private String description;
}
