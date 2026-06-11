package org.dromara.system.domain.bo;

import org.dromara.system.domain.SqlModel;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * sql模型业务对象 sys_sql_model
 *
 * @author Lion Li
 * @date 2026-06-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SqlModel.class, reverseConvertGenerate = false)
public class SqlModelBo extends BaseEntity {

    /**
     * 唯一ID
     */
    @NotBlank(message = "唯一ID不能为空")
    private String id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 主键类型（0数值 1字符）
     */
    @NotBlank(message = "主键类型（0数值 1字符）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String javaType;

    /**
     * sql语句
     */
    @NotBlank(message = "sql语句不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sqlText;

    /**
     * 模型描述
     */
    private String description;


}
