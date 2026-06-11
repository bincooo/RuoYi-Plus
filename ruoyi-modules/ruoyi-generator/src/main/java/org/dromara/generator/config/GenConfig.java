package org.dromara.generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "gen")
@PropertySource(value = {"classpath:generator.yml"}, encoding = "UTF-8")
public class GenConfig {

    /**
     * 作者
     */
    public static String author;

    /**
     * 生成包路径
     */
    public static String packageName;

    /**
     * 自动去除表前缀，默认是false
     */
    public static boolean autoRemovePre;

    /**
     * 表前缀(类名不会包含表前缀)
     */
    public static String tablePrefix;

    /**
     * 后端模板生成路径
     */
    public static String javaPath;

    /**
     * 前端模板生成路径
     */
    public static String reactPath;

    @Value("${author}")
    public void setAuthor(String author) {
        GenConfig.author = author;
    }

    @Value("${packageName}")
    public void setPackageName(String packageName) {
        GenConfig.packageName = packageName;
    }

    public static boolean getAutoRemovePre() {
        return autoRemovePre;
    }

    @Value("${autoRemovePre}")
    public void setAutoRemovePre(boolean autoRemovePre) {
        GenConfig.autoRemovePre = autoRemovePre;
    }

    @Value("${tablePrefix}")
    public void setTablePrefix(String tablePrefix) {
        GenConfig.tablePrefix = tablePrefix;
    }


    @Value("${javaPath}")
    public void setJavaPath(String javaPath) {
        GenConfig.javaPath = javaPath;
    }

    @Value("${reactPath}")
    public void setReactPath(String reactPath) {
        GenConfig.reactPath = reactPath;
    }
}
