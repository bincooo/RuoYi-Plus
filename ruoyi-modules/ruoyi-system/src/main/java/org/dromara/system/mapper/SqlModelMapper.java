package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.SelectProvider;
import org.dromara.system.domain.SysSqlModel;
import org.dromara.system.domain.vo.SysSqlModelVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.vo.SqlQueryVo;

import java.util.List;
import java.util.Map;

/**
 * sql模型Mapper接口
 *
 * @author Lion Li
 * @date 2026-06-04
 */
public interface SqlModelMapper extends BaseMapperPlus<SysSqlModel, SysSqlModelVo> {

    @SelectProvider(type = SqlProvider.class, method = "executeSql")
    List<SqlQueryVo> executeSql(IPage<SqlQueryVo> page, Map<String, Object> params);

    class SqlProvider {
        public String executeSql(IPage<SqlQueryVo> page, Map<String, Object> params) {
            String sql = (String) params.get("_sql");
            StringBuilder sbr = new StringBuilder();
            sbr.append("select id, code, name, description from (")
                .append(sql)
                .append(") t where 1=1 ");
            if (params.containsKey("id")) {
                sbr.append("and id = #{params.id} ");
            }
            if (params.containsKey("code")) {
                sbr.append("and code like concat('%', #{params.code}, '%') ");
            }
            if (params.containsKey("name")) {
                sbr.append("and name like concat('%', #{params.name}, '%') ");
            }
            return sbr.toString();
        }
    }
}
