package com.qpp.apicommons.annotation;

import java.lang.annotation.*;


/**
 * @ClassName SysLoginController
 * @Description TODO 数据权限过滤注解
 * @Author qipengpai
 * @Date 2018/10/25 11:41
 * @Version 1.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 表的别名
     */
    String tableAlias() default "";
}
