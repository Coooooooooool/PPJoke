package com.alex.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ActivityDestination {

    //页面生成标志
    String pageUrl();

    //是否需要登录
    boolean needLogin() default false;

    //是否是起始页
    boolean asStarter() default false;

}
