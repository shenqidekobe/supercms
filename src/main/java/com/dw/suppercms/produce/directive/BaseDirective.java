package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dw.suppercms.application.system.ProduceLogService;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.FreemarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 基类
 * 
 * @author kobe
 */
public abstract class BaseDirective implements TemplateDirectiveModel {
	
	protected Logger log = LoggerFactory.getLogger(getClass()); 
	
	
	protected static final Integer DEFAULT_PAGE_SIZE = 10;
	
	@Resource
	private ProduceLogService produceLogService;
	
	/**
	 * 记录指令解析错误日志
	 * @param desc描述信息
	 * @param errorMsg错误信息
	 * */
	protected void saveDirectiveParseErrorLog(String desc,String errorMsg){
		User user=CommonsUtil.getPrincipal();
		ProduceLogInfo produceLog=new ProduceLogInfo();
		produceLog.setUserId(user==null?null:user.getId());
		produceLog.setProduceType(MakeEventType.DIRECTIVE.getKey());
		produceLog.setProduceResult(MAKE_RESULT.FAIL.toString());
		produceLog.setProduceDesc(desc);
		produceLog.setProduceDate(new Date());
		produceLog.setProduceResultMsg(errorMsg);
		produceLogService.createProduceLog(produceLog);
	}
	

	/**
	 * 得到HttpServletRequest
	 */
	protected HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
	}
	
	/**
	 * 设置局部变量
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            变量值
	 * @param env
	 *            Environment
	 * @param body
	 *            TemplateDirectiveBody
	 */
	protected void setLocalVariable(String name, Object value, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateModel sourceVariable = FreemarkerUtils.getVariable(name, env);
		FreemarkerUtils.setVariable(name, value, env);
		body.render(env.getOut());
		FreemarkerUtils.setVariable(name, sourceVariable, env);
	}

	/**
	 * 设置局部变量
	 * 
	 * @param variables
	 *            变量
	 * @param env
	 *            Environment
	 * @param body
	 *            TemplateDirectiveBody
	 */
	protected void setLocalVariables(Map<String, Object> variables, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		Map<String, Object> sourceVariables = new HashMap<String, Object>();
		for (String name : variables.keySet()) {
			TemplateModel sourceVariable = FreemarkerUtils.getVariable(name, env);
			sourceVariables.put(name, sourceVariable);
		}
		FreemarkerUtils.setVariables(variables, env);
		body.render(env.getOut());
		FreemarkerUtils.setVariables(sourceVariables, env);
	}

}