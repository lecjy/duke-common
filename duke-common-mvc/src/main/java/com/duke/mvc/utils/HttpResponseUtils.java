package com.duke.mvc.utils;

import com.duke.common.base.Result;
import com.duke.common.base.enums.ResultEnum;
import com.duke.common.base.utils.JacksonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpResponseUtils {

    private HttpResponseUtils() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static <T> void response(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        // spring 5.2之后不需要指定utf-8，默认就是utf-8
        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.setStatus(status.value());
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Message");
        response.addHeader("Message", URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    public static void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response(response, HttpStatus.UNAUTHORIZED, message);
    }

    public static <T> void okResponse(HttpServletResponse response, T data) throws IOException {
        response(response, HttpStatus.OK, null);
    }

    public static void okResponse(HttpServletResponse response) throws IOException {
        okResponse(response, null);
    }
}
