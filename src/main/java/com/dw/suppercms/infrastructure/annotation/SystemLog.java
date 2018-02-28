package com.dw.suppercms.infrastructure.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;

/**
 * 系统日志自定义注解(方法注解)
 * 需要保存日志的必须在方法上注解该注解
 * */
@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLog {
	
	public String operation();//操作名称
	
	public OPER_TYPE operType();//操作类型
	
	public String operId() default "";//操作ID
	
	public String operDesc() default "";//操作描述
	
    boolean isSaveRequestData() default true;//是否保存请求的参数 

}
