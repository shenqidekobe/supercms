package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.plugin.PlugInManageService;
import com.dw.suppercms.domain.plugin.PlugInInfo;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 插件指令
 * @author kobe
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class PlugInDirective extends BaseDirective{

	private static final String PARAM_NAME = "name"; //名称
	
	@Resource
	private PlugInManageService plugInManageService;
	
	@Override
	public void execute(Environment env,  Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Object namevalue = params.get(PARAM_NAME);
		if(namevalue == null){
			throw new TemplateException("插件指令未指定属性：" + PARAM_NAME, env);
		}
		PlugInInfo plug = plugInManageService.findPlugInByTag(namevalue.toString());
		if(plug!=null&&StringUtils.isNotEmpty(plug.getPlugContent())){
			String plugContent = plug.getPlugContent();
			StringBuffer sb=new StringBuffer();
			sb.append("<script type=\"text/javascript\">\r\n");
			for(Object key:params.keySet()){
				sb.append("var "+key.toString()+"='"+params.get(key).toString()+"';\r\n");
			}
			if(StringUtils.isNotEmpty(plug.getJsDefinition())){
				sb.append(plug.getJsDefinition()+"\r\n");//定义js常量,如{var domain='';}
			}
			sb.append("</script>\r\n");
			sb.append(plugContent);
			env.getOut().write(sb.toString());
			if(body==null) {
				body = new TemplateDirectiveBody() {
					@Override
					public void render(Writer writer) throws TemplateException, IOException {
					}
				};
			}
			body.render(env.getOut());
		}else{
			String desc=String.format("插件名=%s 未添加任务内容",namevalue.toString());
			saveDirectiveParseErrorLog(desc, "未找到任何插件信息");
		}
	}
	
}
