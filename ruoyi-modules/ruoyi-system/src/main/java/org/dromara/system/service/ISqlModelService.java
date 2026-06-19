package org.dromara.system.service;

import jakarta.validation.constraints.NotEmpty;
import org.dromara.system.domain.bo.SysSqlModelBo;
import org.dromara.system.domain.vo.SysSqlModelVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.system.domain.vo.SqlQueryVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * sql模型Service接口
 *
 * @author Lion Li
 * @date 2026-06-04
 */
public interface ISqlModelService {

    /**
     * 查询sql模型
     *
     * @param id 主键
     * @return sql模型
     */
    SysSqlModelVo queryById(String id);

    /**
     * 分页查询sql模型列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return sql模型分页列表
     */
    TableDataInfo<SysSqlModelVo> queryPageList(SysSqlModelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的sql模型列表
     *
     * @param bo 查询条件
     * @return sql模型列表
     */
    List<SysSqlModelVo> queryList(SysSqlModelBo bo);

    /**
     * 新增sql模型
     *
     * @param bo sql模型
     * @return 是否新增成功
     */
    Boolean insertByBo(SysSqlModelBo bo);

    /**
     * 修改sql模型
     *
     * @param bo sql模型
     * @return 是否修改成功
     */
    Boolean updateByBo(SysSqlModelBo bo);

    /**
     * 校验并批量删除sql模型信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    TableDataInfo<SqlQueryVo> executeSql(@NotEmpty(message = "主键不能为空") String id, Map<String, Object> params, PageQuery pageQuery);
}
