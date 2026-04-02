package com.xelops.actionplan.config.logging;

import com.xelops.actionplan.enumeration.ModuleEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class ActionPlanApiLoggerAspect {

    @Around("@annotation(ActionPlanPlatformLogger)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String resourceName = joinPoint.getTarget().getClass().getSimpleName();

        var annotation = method.getAnnotation(ActionPlanPlatformLogger.class);
        var modules = Arrays.stream(annotation.module())
                .map(ModuleEnum::getName).toList();


        log.info(
                "#({}): Start {} {} - {}({})",
                modules,
                annotation.layer(),
                resourceName,
                method.getName(),
                joinPoint.getArgs()
        );

        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Exception e) {
            log.error("#({}): Error occurred while executing {}{}({}) with message: {} in {}ms",
                    modules, resourceName, method.getName(), joinPoint.getArgs(),
                    e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }

        log.info(
                "#({}): End {} {} - {}({}) in {}ms",
                modules,
                annotation.layer(),
                resourceName,
                method.getName(),
                joinPoint.getArgs(),
                System.currentTimeMillis() - start
        );
        return proceed;
    }
}
