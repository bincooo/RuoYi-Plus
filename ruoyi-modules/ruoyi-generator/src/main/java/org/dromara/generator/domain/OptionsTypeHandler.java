package org.dromara.generator.domain;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Map;

@MappedTypes({Map.class})
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.OTHER})
public class OptionsTypeHandler extends AbstractJsonTypeHandler<Map<String, Object>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public OptionsTypeHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Map<String, Object> parse(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析失败: " + json, e);
        }
    }

    @Override
    public String toJson(Map<String, Object> obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 转换失败", e);
        }
    }
}
