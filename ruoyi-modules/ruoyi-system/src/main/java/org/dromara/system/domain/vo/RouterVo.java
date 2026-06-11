package org.dromara.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 路由配置信息
 *
 * @author Lion Li
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 其他元素
     */
    private MetaVo handle;

    /**
     * 子路由
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RouterVo> children;

}
