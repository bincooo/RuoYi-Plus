package org.dromara.system.domain.vo;

import org.dromara.system.domain.SqlModel;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * sql模型视图对象 sys_sql_model
 *
 * @author Lion Li
 * @date 2026-06-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SqlModel.class)
public class SqlModelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @ExcelProperty(value = "唯一ID")
    private String id;

    /**
     * 模型名称
     */
    @ExcelProperty(value = "模型名称")
    private String name;

    /**
     * 主键类型（0数值 1字符）
     */
    @ExcelProperty(value = "主键类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=数值,1=字符")
    private String javaType;

    /**
     * sql语句
     */
    @ExcelProperty(value = "sql语句")
    private String sqlText;

    /**
     * 模型描述
     */
    @ExcelProperty(value = "模型描述")
    private String description;


}
