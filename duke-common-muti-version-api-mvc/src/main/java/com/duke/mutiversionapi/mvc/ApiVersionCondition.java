package com.duke.mutiversionapi.mvc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * 如果能直接重写HeadersRequestCondition是最简单的，但它是final的
 */
@Slf4j
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private static final Pattern versionPattern = Pattern.compile("^\\d+(\\.\\d+)*$");

    @Getter
    private final String apiVersion;

    @Getter
    private final String paramName;

    public ApiVersionCondition(String paramName, String apiVersion) {
        this.apiVersion = apiVersion;
        this.paramName = paramName;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.paramName, other.apiVersion);
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String requestVersion = request.getHeader(paramName);
        if (requestVersion == null) {
            requestVersion = request.getParameter(paramName);
        }
        return support(requestVersion);
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return compareVersion(other.getApiVersion(), this.apiVersion);
    }

    // 使用VERSION_LIST作用是过虑不支持的版本号
    private ApiVersionCondition support(String requestVersion) {
        boolean support = requestVersion != null &&
                versionPattern.matcher(requestVersion).matches() &&
                compareVersion(requestVersion, this.apiVersion) >= 0;
        if (support) {
            log.info("api version {}   support request version={}", this.apiVersion, requestVersion);
            return this;
        }
        log.warn("api version {} unsupport request version={}", this.apiVersion, requestVersion);
        return null;
    }

    private int compareVersion(String requestVersion, String apiVersion) {
        if (this.apiVersion.equals(requestVersion)) {
            return 0;
        }
        if (requestVersion == null || apiVersion == null) {
            throw new RuntimeException("compare version error:illegal params.");
        }
        String[] requestVersionArray = requestVersion.split("\\.");
        String[] apiVersionArray = apiVersion.split("\\.");
        int idx = 0;
        int minLength = Math.min(requestVersionArray.length, apiVersionArray.length);
        int diff = 0;
        while (idx < minLength && (diff = Integer.parseInt(requestVersionArray[idx]) - Integer.parseInt(apiVersionArray[idx])) == 0) {
            ++idx;
        }
        return diff;
    }

//    @Override
//    public int hashCode() {
//        return this.apiVersion.hashCode();
//    }
}
