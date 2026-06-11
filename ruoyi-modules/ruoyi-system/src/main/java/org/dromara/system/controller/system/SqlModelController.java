package org.dromara.system.controller.system;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.system.domain.vo.SqlQueryVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.system.domain.vo.SqlModelVo;
import org.dromara.system.domain.bo.SqlModelBo;
import org.dromara.system.service.ISqlModelService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * sql模型
 *
 * @author Lion Li
 * @date 2026-06-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/sqlModel")
public class SqlModelController extends BaseController {

    private final ISqlModelService sqlModelService;

    /**
     * 查询sql模型列表
     */
    @SaCheckPermission("system:sqlModel:page")
    @GetMapping("/page")
    public TableDataInfo<SqlModelVo> list(SqlModelBo bo, PageQuery pageQuery) {
        return sqlModelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出sql模型列表
     */
    @SaCheckPermission("system:sqlModel:export")
    @Log(title = "sql模型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SqlModelBo bo, HttpServletResponse response) {
        List<SqlModelVo> list = sqlModelService.queryList(bo);
        ExcelUtil.exportExcel(list, "sql模型", SqlModelVo.class, response);
    }

    /**
     * 获取sql模型详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:sqlModel:page")
    @GetMapping("/{id}")
    public R<SqlModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(sqlModelService.queryById(id));
    }

    /**
     * 新增sql模型
     */
    @SaCheckPermission("system:sqlModel:create")
    @Log(title = "sql模型", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SqlModelBo bo) {
        return toAjax(sqlModelService.insertByBo(bo));
    }

    /**
     * 修改sql模型
     */
    @SaCheckPermission("system:sqlModel:update")
    @Log(title = "sql模型", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SqlModelBo bo) {
        return toAjax(sqlModelService.updateByBo(bo));
    }

    /**
     * 删除sql模型
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:sqlModel:delete")
    @Log(title = "sql模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(sqlModelService.deleteWithValidByIds(List.of(ids), true));
    }

    @PostMapping("/executeSql/{id}")
    public TableDataInfo<SqlQueryVo> executeSql(@NotEmpty(message = "主键不能为空")
                          @PathVariable String id, @RequestBody Map<String, Object> params, PageQuery pageQuery) {
        return sqlModelService.executeSql(id, params, pageQuery);
    }
}
