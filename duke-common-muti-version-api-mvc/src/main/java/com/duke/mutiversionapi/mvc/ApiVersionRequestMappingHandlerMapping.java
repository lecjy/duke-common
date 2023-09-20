package com.duke.mutiversionapi.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Value("${duke.api.muti.version.param.name:api-version}")
    private String paramName;

    @Override
    protected RequestCondition<?> getCustomTypeCondition(@NonNull Class<?> handlerType) {
        return createCondition(handlerType.getDeclaredAnnotation(SupportApiVersion.class));
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(@NonNull Method method) {
        return createCondition(method.getDeclaredAnnotation(SupportApiVersion.class));
    }

    private RequestCondition<?> createCondition(SupportApiVersion apiVersion) {
        return null == apiVersion ? null : new ApiVersionCondition(paramName, apiVersion.value());
    }

    /**
     @Override protected RequestCondition<?> getCustomTypeCondition(@NonNull Class<?> handlerType) {
     //        RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
     RequestMapping mapping = handlerType.getAnnotation(RequestMapping.class);
     if (mapping != null || mapping.headers() == null || mapping.headers().length > 0) {
     ApiVersionCondition match = match(mapping.headers());
     if (match != null) {
     return match;
     }
     }
     return super.getCustomTypeCondition(handlerType);
     }

     @Override protected RequestCondition<?> getCustomMethodCondition(@NonNull Method method) {
     //        RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
     Annotation[] annotations = method.getAnnotations();
     String[] headers = null;
     for (Annotation annotation : annotations) {
     if (annotation instanceof RequestMapping mapping) {
     headers = mapping.headers();
     }
     if (annotation instanceof GetMapping mapping) {
     headers = mapping.headers();
     }
     if (annotation instanceof PostMapping mapping) {
     headers = mapping.headers();
     }
     if (annotation instanceof PutMapping mapping) {
     headers = mapping.headers();
     }
     if (annotation instanceof DeleteMapping mapping) {
     headers = mapping.headers();
     }
     if (annotation instanceof PatchMapping mapping) {
     headers = mapping.headers();
     }
     }
     if (headers != null && headers.length > 0) {
     ApiVersionCondition match = match(headers);
     if (match != null) {
     return match;
     }
     }
     return super.getCustomMethodCondition(method);
     }


     private ApiVersionCondition match(String[] headers) {
     for (String header : headers) {
     String[] split = header.split("=");
     if (split.length != 2) {
     continue;
     }
     if (paramName.equals(split[0].trim())) {
     return new ApiVersionCondition(paramName, split[1].trim());
     }
     }
     return null;
     }
     */
}
 