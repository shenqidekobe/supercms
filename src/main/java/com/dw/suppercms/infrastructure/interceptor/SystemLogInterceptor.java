package com.dw.suppercms.infrastructure.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dw.suppercms.application.system.SysLogService;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_STATE;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;

import flexjson.JSONSerializer;

/**
 * 系统日志拦截器
 * @date 2015-7-9
 * */
public class SystemLogInterceptor extends HandlerInterceptorAdapter{
	
	private static Logger logger=Logger.getLogger(SystemLogInterceptor.class);
	
	@Autowired
	private SysLogService sysLogService;
	
	@Override
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		try {
			if(handler instanceof HandlerMethod){
				HandlerMethod handlerMethod=(HandlerMethod)handler;
				Method method=handlerMethod.getMethod();
				SystemLog systemLog=method.getAnnotation(SystemLog.class);
				if(systemLog!=null){
					handlerLog(systemLog, method.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存系统日志失败..."+e);
		}
	}
	
	/**
	 * 保存日志
	 * */
	@SuppressWarnings("rawtypes")
	private void handlerLog(SystemLog systemLog,String action){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String path=request.getServletPath();
		Map<String, String[]> paramsMap=request.getParameterMap();
		StringBuffer params=new StringBuffer();
		String operId=systemLog.operId();
		String title=systemLog.operation();
		SysLogInfo.OPER_TYPE operType=systemLog.operType();
		Long userId=CommonsUtil.getPrincipal()==null?null:CommonsUtil.getPrincipal().getId();
		String loginName=CommonsUtil.getPrincipal()==null?null:CommonsUtil.getPrincipal().getUsername();
		//when requestMethod is POST (execute save method),need get entity of id
		if(StringUtils.isEmpty(operId)){
			operId=(String)request.getAttribute(SysLogInfo.LOG_PRIMARY_ID);
		}
		
		if(!paramsMap.isEmpty()){
			params.append(new JSONSerializer().deepSerialize(paramsMap));
			if(StringUtils.isEmpty(operId)){
				operId=paramsMap.get("id")!=null?paramsMap.get("id")[0]:"";
			}
		}
			
		//处理请求的RequestBody参数，如任务切换状态，需要从参数中得到操作类型和名称
		Object obj=(Object)request.getAttribute(SysLogInfo.LOG_REQUEST_BODY);
		if(obj!=null){
			params.append(new JSONSerializer().deepSerialize(obj));
			if(obj instanceof Map){
				Map map=(Map) obj;
				//处理任务状态切换
				if(map.get("taskState")!=null){
					String taskState=map.get("taskState").toString();
					if(taskState.equals(TASK_STATE.Enable.toString())){
						operType=OPER_TYPE.enable;
						title="启用"+title;
					}else{
						operType=OPER_TYPE.disable;
						title="停用"+title;
					}
				}
			}
			if(StringUtils.isEmpty(operId)){
				try {
					Field[] fields=obj.getClass().getSuperclass().getDeclaredFields();
					for(Field field:fields){
						if(field.getName().equals("id")){
							field.setAccessible(true);
							operId=field.get(obj).toString();
						}
					}
				} catch (Exception e) {
				}
			}
		}
		SysLogInfo.MODULE_LOG moduleLog=SysLogInfo.MODULE_LOG.system_module;
		if(operType.equals(SysLogInfo.OPER_TYPE.login)||operType.equals(SysLogInfo.OPER_TYPE.logout)){
			moduleLog=SysLogInfo.MODULE_LOG.system_login;
		}
		String operIp=CommonsUtil.getIPAddress(request);
		String operDesc=systemLog.operDesc();
		SysLogInfo log=new SysLogInfo();
		log.setOperId(operId);
		log.setOperation(title);
		log.setAction(action);
		log.setUserId(userId);
		log.setLoginName(loginName);
		log.setOperDesc(path+":"+operDesc);
		log.setOperParam("params:"+params);
		log.setOperType(operType);
		log.setModuleLog(moduleLog);
		log.setOperIp(operIp);
		log.setOperTime(new Date());
		this.sysLogService.createSysLog(log);
	}
}
