package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * sql模型对象 sys_sql_model
 *
 * @author Lion Li
 * @date 2026-06-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_sql_model")
public class SqlModel extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 主键类型（0数值 1字符）
     */
    private String javaType;

    /**
     * sql语句
     */
    private String sqlText;

    /**
     * 模型描述
     */
    private String description;


}
