package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.domain.vo.SqlQueryVo;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.SqlModelBo;
import org.dromara.system.domain.vo.SqlModelVo;
import org.dromara.system.domain.SqlModel;
import org.dromara.system.mapper.SqlModelMapper;
import org.dromara.system.service.ISqlModelService;

import java.util.*;

/**
 * sql模型Service业务层处理
 *
 * @author Lion Li
 * @date 2026-06-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SqlModelServiceImpl implements ISqlModelService {

    private final SqlModelMapper baseMapper;

    /**
     * 查询sql模型
     *
     * @param id 主键
     * @return sql模型
     */
    @Override
    public SqlModelVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询sql模型列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return sql模型分页列表
     */
    @Override
    public TableDataInfo<SqlModelVo> queryPageList(SqlModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SqlModel> lqw = buildQueryWrapper(bo);
        Page<SqlModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的sql模型列表
     *
     * @param bo 查询条件
     * @return sql模型列表
     */
    @Override
    public List<SqlModelVo> queryList(SqlModelBo bo) {
        LambdaQueryWrapper<SqlModel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SqlModel> buildQueryWrapper(SqlModelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SqlModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getId()), SqlModel::getId, bo.getId());
        lqw.orderByAsc(SqlModel::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), SqlModel::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增sql模型
     *
     * @param bo sql模型
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SqlModelBo bo) {
        SqlModel add = MapstructUtils.convert(bo, SqlModel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改sql模型
     *
     * @param bo sql模型
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SqlModelBo bo) {
        SqlModel update = MapstructUtils.convert(bo, SqlModel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SqlModel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除sql模型信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public TableDataInfo<SqlQueryVo> executeSql(String id, Map<String, Object> params, PageQuery pageQuery) {
        SqlModelVo sqlModelVo = baseMapper.selectVoById(id);
        if (sqlModelVo == null) {
            throw new IllegalArgumentException("标识符不存在");
        }
        if (params == null) {
            params = new HashMap<>();
        }

        params.put("_sql", sqlModelVo.getSqlText());
        Object pkId = params.get("id");
        if (pkId != null) {
            String javaType = sqlModelVo.getJavaType();
            if (Objects.equals(javaType, "0")) {
                pkId = Long.valueOf(pkId.toString());
            } else {
                pkId = pkId.toString();
            }
            params.put("id", pkId);
        }
        List<SqlQueryVo> list = baseMapper.executeSql(pageQuery.build(), params);
        return TableDataInfo.build(list);
    }
}
