package com.geekhub.loggin;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = Logger.getLogger("SERVICE LAYER");

    @Before("execution(* com.geekhub.services.*.*(..))")
    public void logBefore(JoinPoint joinpoint) {
        Object className = joinpoint.getTarget().getClass().getSimpleName();
        String methodName = joinpoint.getSignature().getName();
        log.info("INVOKE " + className + "." + methodName);
    }

    @After("execution(* com.geekhub.services.*.*(..))")
    public void logAfter(JoinPoint joinpoint) {
        Object className = joinpoint.getTarget().getClass().getSimpleName();
        String methodName = joinpoint.getSignature().getName();
        log.info("END PERFORM " + className + "." + methodName);
    }

    @AfterThrowing(value = "execution(* com.geekhub.services.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinpoint, Exception ex) {
        Object className = joinpoint.getTarget().getClass().getSimpleName();
        String methodName = joinpoint.getSignature().getName();
        log.info("THROW " + ex.getMessage() + " FROM " + className + "." + methodName);
    }
}
