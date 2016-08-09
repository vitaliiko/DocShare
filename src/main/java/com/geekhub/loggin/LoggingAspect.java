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
        log.info(ConsoleConstants.COLOR_GREEN + "INVOKE " + ConsoleConstants.COLOR_DEFAULT +
                className + "." + methodName);
    }

    @After("execution(* com.geekhub.services.*.*(..))")
    public void logAfter(JoinPoint joinpoint) {
        Object className = joinpoint.getTarget().getClass().getSimpleName();
        String methodName = joinpoint.getSignature().getName();
        log.info(ConsoleConstants.COLOR_YELLOW + "END PERFORM " + ConsoleConstants.COLOR_DEFAULT +
                className + "." + methodName);
    }

    @AfterThrowing(value = "execution(* com.geekhub.services.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinpoint, Exception ex) {
        Object className = joinpoint.getTarget().getClass().getSimpleName();
        String methodName = joinpoint.getSignature().getName();
        log.info(ConsoleConstants.COLOR_RED + "THROW " + ex.getMessage() + ConsoleConstants.COLOR_DEFAULT + " FROM " +
                className + "." + methodName);
    }
}
