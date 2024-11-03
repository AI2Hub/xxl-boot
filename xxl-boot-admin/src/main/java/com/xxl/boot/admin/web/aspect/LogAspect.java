package com.xxl.boot.admin.web.aspect;

import com.xxl.boot.admin.annotation.Log;
import com.xxl.boot.admin.model.dto.LoginUserDTO;
import com.xxl.boot.admin.model.entity.XxlBootLog;
import com.xxl.boot.admin.service.LogService;
import com.xxl.boot.admin.service.impl.LoginService;
import com.xxl.boot.admin.util.Ip2regionUtil;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    private LoginService loginService;
    @Resource
    private LogService logService;


    /**
     * 定义切点，匹配所有标记了 @Log 注解的方法
     */
    @Pointcut("@annotation(com.xxl.boot.admin.annotation.Log)")
    public void logPointcut() {}

    /**
     * 在方法调用前记录请求信息
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    //@Around("@annotation(com.xxl.boot.admin.annotation.Log)")
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        /*logger.info("Request URL: {}", request.getRequestURL());
        logger.info("HTTP Method: {}", request.getMethod());
        logger.info("IP: {}", request.getRemoteAddr());
        logger.info("Class Method: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        logger.info("Arguments: {}", joinPoint.getArgs());*/

        // valid log
        Log log = AnnotationUtils.findAnnotation(method, Log.class);
        if (log == null) {
            return joinPoint.proceed();
        }

        // proceed
        long startTime = 0;
        Object result = null;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            result = joinPoint.proceed();
            endTime = System.currentTimeMillis();
        } catch (Throwable e) {
            throw e;
        } finally {
            // TODO 待改造，推送队列，异步消费。
            try {
                log(log, request, joinPoint, result, startTime, endTime);
            } catch (Exception e) {
                // ignore
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    private void log(Log log, HttpServletRequest request, ProceedingJoinPoint joinPoint, Object result, long startTime, long endTime) {
        // process
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        String operator = loginUser!=null?loginUser.getUsername():"";
        String ip = Ip2regionUtil.getIp(request);
        ip = ip!=null?ip:"";

        // content
        String content = log.content();
        if (StringTool.isBlank(content)) {
            content += "【Request】:" + GsonTool.toJson(request.getParameterMap());     // joinPoint.getArgs()，大对象不符合预期
            content += "\n\n【Response】:" + result;
            content += "\n\n【CostTime】:" + (endTime - startTime) + "ms";
        }

        // generate
        XxlBootLog xxlBootLog = new XxlBootLog();
        xxlBootLog.setType(log.type().getCode());
        xxlBootLog.setModule(log.module().name());
        xxlBootLog.setTitle(log.title());
        xxlBootLog.setContent(content);
        xxlBootLog.setOperator(operator);
        xxlBootLog.setIp(ip);

        logService.insert(xxlBootLog);
    }

}