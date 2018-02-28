package com.dw.suppercms.infrastructure.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dw.suppercms.application.system.SysLogService;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

/**
 * base controller
 * 
 * */
public class BaseController {
	
	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 保存系统操作日志
	 * @param operType:操作类型
	 * @param operation:操作名称
	 * @param operId：数据ID
	 * @param operDesc:操纵描述
	 * */
	protected void saveSystemOperLog(OPER_TYPE operType,String operation,Object operId,String operDesc){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String path=request.getServletPath();
		String params= new JSONSerializer().deepSerialize(request.getParameterMap());
		SysLogInfo.MODULE_LOG moduleLog=SysLogInfo.MODULE_LOG.system_module;
		if(operType.equals(SysLogInfo.OPER_TYPE.login)||operType.equals(SysLogInfo.OPER_TYPE.logout)){
			moduleLog=SysLogInfo.MODULE_LOG.system_login;
		}
		String operIp=CommonsUtil.getIPAddress(request);
		Long userId=null;//getLoginUser().getUserId();
		SysLogInfo log=new SysLogInfo();
		log.setOperation(operation);
		log.setUserId(userId);
		log.setOperId(operId.toString());
		log.setOperDesc(operDesc);
		log.setOperParam(path+",params:"+params);
		log.setOperType(operType);
		log.setModuleLog(moduleLog);
		log.setOperIp(operIp);
		log.setOperTime(new Date());
		this.sysLogService.createSysLog(log);
	}
	
	/**
	 * 获取登录用户
	 * 
	 * @return
	 */
	public User getLoginUser() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			User principal = (User) subject.getPrincipal();
			return principal;
		}
		return null;
	}
	
	protected JSONSerializer JSONSerializer(){
		JSONSerializer serializer = new JSONSerializer();
		serializer.exclude("*.class")
				.transform(new DateTransformer("yyyy-MM-dd HH:mm:ss"), Date.class);
		return serializer;
	}
	
	/**
	 * 往日志插入RequestBody对象
	 * 如新增数据和编辑数据对象的时候，需在方法后调用该方法，传入RequestBody的对象
	 * */
	protected void requestBodyAsLog(Object obj) {
		if(obj!=null){
			RequestAttributes requestAttributes=RequestContextHolder.currentRequestAttributes();
			requestAttributes.setAttribute(SysLogInfo.LOG_REQUEST_BODY, obj, RequestAttributes.SCOPE_REQUEST);
		}
	}

}
