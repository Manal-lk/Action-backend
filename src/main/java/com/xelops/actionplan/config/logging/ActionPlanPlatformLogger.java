package com.xelops.actionplan.config.logging;

import com.xelops.actionplan.enumeration.ModuleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionPlanPlatformLogger {

    ModuleEnum[] module();
    String layer() default "Resource";
}
