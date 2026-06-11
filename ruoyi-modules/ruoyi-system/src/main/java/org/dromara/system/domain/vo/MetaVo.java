package org.dromara.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 路由显示信息
 *
 * @author ruoyi
 */

@Data
@Builder
public class MetaVo {

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private Boolean hideInMenu;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    private Boolean keepAlive;

    /**
     * 内链地址（http(s)://开头）
     */
    private String iframeLink;

    /**
     * 激活菜单
     */
    private String activeMenu;

    /**
     * 菜单排序，用于控制侧边栏菜单的显示顺序。
     */
    private Integer order;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> permissions;

}
