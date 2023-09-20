package com.duke.jsonview;

import com.duke.common.base.Result;
import com.duke.mybatis.page.Page;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JSONViewConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonMapper viewJsonMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION) // 解决 @JsonView 无法过滤bug
                .build();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Result.class, new JsonSerializer<>() {
            @Override
            public void serialize(Result result, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeStartObject();
                gen.writeStringField("message", result.getMessage());
                gen.writeNumberField("code", result.getCode());
                serializeData(gen, provider, result.getData(), viewJsonMapper);
            }
        });
        simpleModule.addSerializer(Page.class, new JsonSerializer<>() {
            @Override
            public void serialize(Page page, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeStartObject();
                gen.writeNumberField("totalRecord", page.getTotalRecord());
                serializeData(gen, provider, page.getData(), viewJsonMapper);
            }
        });
        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
//                .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .addModule(simpleModule)
                .build();
    }

    public void serializeData(JsonGenerator gen, SerializerProvider provider, Object data, JsonMapper viewJsonMapper) throws IOException {
        Class<?> activeView = provider.getActiveView();
        if (activeView == null) {
            gen.writeObjectField("data", data);
        } else {
            gen.writeFieldName("data");
            viewJsonMapper.writerWithView(activeView).writeValue(gen, data);
        }
        gen.writeEndObject();
    }
}
