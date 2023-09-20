package com.duke.common.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;


public class JacksonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void serializeData(JsonGenerator gen, SerializerProvider provider, Object data, JsonMapper viewJsonMapper) throws IOException {
        Class<?> activeView = provider.getActiveView();
        if (activeView == null) {
            gen.writeObjectField("data", data);
        } else {
            gen.writeFieldName("data");
            viewJsonMapper.writerWithView(activeView).writeValue(gen, data);
        }
        gen.writeEndObject();
    }


    private JacksonUtils() {
        throw new RuntimeException("Unsupport com.duke.operation");
    }

    public static <T> List<T> toList(@NonNull String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, getCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            String className = clazz.getSimpleName();
            throw new RuntimeException(" parse json " + json + " to class [" + className + "] error：" + e.getMessage() + "");
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T parseObject(@NonNull String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            String className = clazz.getSimpleName();
            throw new RuntimeException(" parse json " + json + " to class [" + className + "] error：" + e.getMessage() + "");
        }
    }

    public static String toJSONString(Object obj, JsonInclude.Include include) {
        try {
            if (include != null) {
                objectMapper.setSerializationInclusion(include);
            }
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("convert " + obj + " to string error：" + e.getMessage() + "");
        }
    }

    public static JsonNode toJSON(Object obj) {
        try {
            return objectMapper.readTree(toJSONString(obj));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("convert " + obj + " to JsonNode error：" + e.getMessage() + "");
        }
    }

    public static String toJSONString(Object obj) {
        return toJSONString(obj, null);
    }

    public static <T> T toObject(String str, Class<T> clazz) {
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("parse json " + str + " to class [" + clazz + "] error：" + e.getMessage() + "");
        }
    }

    public static <T> T toObject(String str, TypeReference<T> typeReference) {
        try {
            return StringUtils.isEmpty(str) ? null : objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("parse json " + str + " to class [" + typeReference + "] error：" + e.getMessage() + "");
        }
    }
}
