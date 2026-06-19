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
import org.dromara.system.domain.bo.SysSqlModelBo;
import org.dromara.system.domain.vo.SqlQueryVo;
import org.dromara.system.domain.vo.SysSqlModelVo;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.SysSqlModel;
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
    public SysSqlModelVo queryById(String id){
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
    public TableDataInfo<SysSqlModelVo> queryPageList(SysSqlModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysSqlModel> lqw = buildQueryWrapper(bo);
        Page<SysSqlModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的sql模型列表
     *
     * @param bo 查询条件
     * @return sql模型列表
     */
    @Override
    public List<SysSqlModelVo> queryList(SysSqlModelBo bo) {
        LambdaQueryWrapper<SysSqlModel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysSqlModel> buildQueryWrapper(SysSqlModelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysSqlModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getId()), SysSqlModel::getId, bo.getId());
        lqw.orderByAsc(SysSqlModel::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysSqlModel::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增sql模型
     *
     * @param bo sql模型
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysSqlModelBo bo) {
        SysSqlModel add = MapstructUtils.convert(bo, SysSqlModel.class);
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
    public Boolean updateByBo(SysSqlModelBo bo) {
        SysSqlModel update = MapstructUtils.convert(bo, SysSqlModel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysSqlModel entity){
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
        SysSqlModelVo sqlModelVo = baseMapper.selectVoById(id);
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
