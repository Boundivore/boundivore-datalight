package cn.boundivore.dl.boot.config;

import cn.boundivore.dl.base.constants.ICommonConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Description: Jackson Mapper Config
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2025/2/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
public class JacksonMapperConfig {

    @Bean(name = "stringFormatObjectMapper")
    @Primary
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = JsonMapper.builder()
                // 忽略JSON字符串中存在但Java对象中不存在的属性
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 忽略空Bean转JSON的错误
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                // 允许字段名不带引号
                .configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
                // 允许使用单引号
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true)
                // 允许数字以0开头
                .configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true)
                // 允许字符串中包含转义字符
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
                // 序列化时排除null值字段
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                // 统一日期格式
                .defaultDateFormat(new SimpleDateFormat(ICommonConstant.DATETIME_FORMAT))
                // 设置时区
                .defaultTimeZone(TimeZone.getTimeZone(ICommonConstant.TIME_ZONE_GMT8))
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
//                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        return objectMapper;
    }
}
