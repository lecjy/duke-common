package com.duke.mvc.unified;

import com.duke.common.base.Result;
import com.duke.common.base.enums.ResultEnum;
import com.duke.common.base.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@ControllerAdvice
@RestControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalResponseBodyHandler.class);
//    @Autowired
//    private PageRequestHolder pageRequestHolder;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
//        Page page = pageRequestHolder.pageRequest();
        if (body instanceof Result) {
//        if (pageRequest != null && result.getData() instanceof List data) {
//            result = Result.builder().success(result.isSuccess()).code(result.getCode()).data(Page.builder().size(pageRequest.getSize()).page(pageRequest.getPage()).totalRecord()..build()).message(result.getMessage()).build();
//        }
            return body;
        }
        return Result.success(body);
    }


    /**
     * Controller上一层相关异常，一个http请求，在到达Controller前，会对该请求的请求信息与目标控制器信息做一系列校验
     */
    @ExceptionHandler(NoHandlerFoundException.class)//首先根据请求Url查找有没有对应的控制器，若没有则会抛该异常，也就是大家非常熟悉的404异常；
    // 增强器上要加上注解@ResponseBody、@ControllerAdvice，否则有些情况下会有死循环或返回类型不对
    // 需要配置
    // spring.mvc.throw-exception-if-no-handler-found=true
    // spring.web.resources.add-mappings=false
    // @ResponseBody
    public Result<?> handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        return Result.error(404, "访问的资源不存在：" + request.getRequestURI());
    }

    //处理form data方式调用接口校验失败抛出的异常，使用form data方式调用接口，校验异常抛出BindException
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
//        pageRequestHolder.remove();
        List<String> collect = new ArrayList<>();
        if (e.getBindingResult().getFieldError() != null) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            collect = fieldErrors.stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        }
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    //处理json请求体调用接口校验失败抛出的异常，使用json请求体调用接口，校验异常抛出MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        pageRequestHolder.remove();
        List<String> collect = new ArrayList<>();
        if (e.getBindingResult().getFieldError() != null) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            collect = fieldErrors.stream()
                    .map(item -> item.getField() + item.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    //处理单个参数校验失败抛出的异常，单个参数校验异常抛出ConstraintViolationException，单个参数校验需要在参数上增加校验注解，并在类上标注@Validated
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handlerConstraintViolationException(ConstraintViolationException e) {
//        pageRequestHolder.remove();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return Result.error(ResultEnum.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), JacksonUtils.toJSONString(collect));
    }

    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable e) {
//        pageRequestHolder.remove();
        log.error("全局异常处理[" + ResultEnum.UNKNOWN_EXCEPTION.getCode() + "[：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.UNKNOWN_EXCEPTION.getCode(), ResultEnum.UNKNOWN_EXCEPTION.getMessage());
    }

//    @ResponseBody
//    @ExceptionHandler
//    public Result<?> handleBusinessException(BusinessException e) {
////        pageRequestHolder.remove();
//        log.error("全局异常处理[" + ResultEnum.BUSINESS_EXCEPTION.getCode() + "[：" + e.getMessage());
//        e.printStackTrace();
//        return Result.error(ResultEnum.BUSINESS_EXCEPTION.getCode(), ResultEnum.BUSINESS_EXCEPTION.getMessage());
//    }

    /**
     * JDBC的SQLException
     */
    @ExceptionHandler(SQLException.class)
    public Result<?> handleSQLException(SQLException e) {
//        pageRequestHolder.remove();
        log.error("全局异常处理[" + ResultEnum.SQL_EXCEPTION.getCode() + "[：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.SQL_EXCEPTION.getCode(), ResultEnum.SQL_EXCEPTION.getMessage());
    }

    /**
     * Spring的DataAccessException
     */
    @ExceptionHandler(DataAccessException.class)
    public Result<?> handleDataAccessException(DataAccessException e) {
//        pageRequestHolder.remove();
        log.error("全局异常处理[" + ResultEnum.DATA_ACCESS_EXCEPTION.getCode() + "[：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.DATA_ACCESS_EXCEPTION.getCode(), ResultEnum.DATA_ACCESS_EXCEPTION.getMessage());
    }

    /**
     * Controller上一层相关异常，一个http请求，在到达Controller前，会对该请求的请求信息与目标控制器信息做一系列校验
     */
    @ExceptionHandler({
//            NoHandlerFoundException.class,//首先根据请求Url查找有没有对应的控制器，若没有则会抛该异常，也就是大家非常熟悉的404异常；
            HttpRequestMethodNotSupportedException.class,//若匹配到了(匹配结果是一个列表，不同的是http方法不同，如：Get、Post等)，则尝试将请求的http方法与列表的控制器做匹配，若没有对应http方法的控制器，则抛该异常；
            HttpMediaTypeNotSupportedException.class,//然后再对请求头与控制器支持的做比较，比如content-type请求头，若控制器的参数签名包含注解@RequestBody，但是请求的content-type请求头的值没有包含application/json，那么会抛该异常(当然，不止这种情况会抛这个异常)；
            MissingPathVariableException.class,//未检测到路径参数。比如url为：/licence/{licenceId}，参数签名包含@PathVariable("licenceId")，当请求的url为/licence，在没有明确定义url为/licence的情况下，会被判定为：缺少路径参数；
            MissingServletRequestParameterException.class,//缺少请求参数。比如定义了参数@RequestParam("licenceId")String licenceId，但发起请求时，未携带该参数，则会抛该异常；
            TypeMismatchException.class,//参数类型匹配失败。比如：接收参数为Long型，但传入的值确是一个字符串，那么将会出现类型转换失败的情况，这时会抛该异常；
            HttpMessageNotReadableException.class,//与上面的HttpMediaTypeNotSupportedException举的例子完全相反，即请求头携带了"content-type: application/json;charset=UTF-8"，但接收参数却没有添加注解@RequestBody，或请求体携带的json串反序列化成pojo的过程中失败了，也会抛该异常；
            HttpMessageNotWritableException.class,//返回的pojo在序列化成json过程失败了，那么抛该异常；
            HttpMediaTypeNotAcceptableException.class,//未知；
            ServletRequestBindingException.class,//未知；
            ConversionNotSupportedException.class,//未知；
            MissingServletRequestPartException.class,//未知；
            AsyncRequestTimeoutException.class//未知；
    })
    public Result<?> handleServletException(Exception e) {
//        pageRequestHolder.remove();
        log.error("全局异常处理[" + ResultEnum.SERVLET_EXCEPTION.getCode() + "[：" + e.getMessage());
        e.printStackTrace();
        return Result.error(ResultEnum.SERVLET_EXCEPTION.getCode(), e.getMessage());
    }
}
