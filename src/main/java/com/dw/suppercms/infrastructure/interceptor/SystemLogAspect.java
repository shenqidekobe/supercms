package com.dw.suppercms.infrastructure.interceptor;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.suppercms.application.system.SysLogService;

/**
 * 系统日志切面拦截机
 * @author kobe
 * */
@Aspect
public class SystemLogAspect {
	
	@Resource
	private SysLogService sysLogService;
	
    private  static  final Logger logger = LoggerFactory.getLogger(SystemLogAspect. class);  
    
    public SystemLogAspect(){
    }
    
    
    @Pointcut("@annotation(com.dw.suppercms.infrastructure.annotation.SystemLog)")
    public void controllerAspect(){}    
    
    
    /**  
     * 方法执行成功后，通知
     *
     * @param joinPoint 切点
     */
    @AfterReturning(value="controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        handleLog(joinPoint,null);
    }
     
	/**
	 * 方法抛出异常后通知
	 * */
    @AfterThrowing(pointcut="controllerAspect()",throwing="e")
    public void doAfter(JoinPoint joinPoint,Exception e)
    {
        handleLog(joinPoint,e);
    }
    
    /**
     * 记录日志到数据库
     * */
    private void handleLog(JoinPoint joinPoint,Exception e){
    	logger.info("切面进入--------------");
    }
    
    

}
