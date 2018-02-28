package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.produce.rule.FreemarkerUtils;
import com.dw.suppercms.produce.rule.VariableDefine;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模版片段指令(常量)
 * 
 */
@Service
public class SnippetDirective extends BaseDirective {

	private static final String PARAM_NAME = "name"; // 常量名称
	private static final String PARAM_VALUE_SID = "{sid}";
    private static final String PARAM_VALUE_CID = "{cid}";
	
	@Resource
	private ProduceFileService produceFileService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Object namevalue = params.get(PARAM_NAME);
		if (namevalue == null) {
			throw new TemplateException("常量指令未指定属性：" + PARAM_NAME, env);
		}
		String name = namevalue.toString();
		if (name.contains(PARAM_VALUE_SID)) {
			if (env.getVariable(VariableDefine.variable_sid) != null) {
				name = name.replace(PARAM_VALUE_SID, env.getVariable(VariableDefine.variable_sid).toString());
			} else {
				log.warn("未找到sid变量");
			}
		}
		if (name.contains(PARAM_VALUE_CID)) {
			if (env.getVariable(VariableDefine.variable_cid) != null) {
				name = name.replace(PARAM_VALUE_CID, env.getVariable(VariableDefine.variable_cid).toString());
			} else {
				log.warn("未找到cid变量");
			}
		}

		TemplateSnippet snippet = produceFileService.findTemplateSnippetByTag(name);
		if(snippet==null)return;
		String snippetContent = snippet.getSnippetCode();
		if (!StringUtils.isEmpty(snippetContent)) {
			if (body == null) {
				body = new TemplateDirectiveBody() {
					@Override
					public void render(Writer writer) throws TemplateException, IOException {}
				};
			}
			//指令封装
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(VariableDefine.directive_cons, new SnippetDirective());
			variables.put(VariableDefine.directive_page,  new PageDirective());
			variables.put(VariableDefine.directive_asign, new AsignDirective());
			variables.put(VariableDefine.directive_rec, new RecordDirective());
			variables.put(VariableDefine.directive_plugin, new PlugInDirective());
			variables.put(VariableDefine.directive_praiseTop, new PraiseTopDirective());
			variables.put(VariableDefine.directive_hitsTop, new HitsTopDirective());
			variables.put(VariableDefine.directive_stat, new StatDirective());
			variables.put(VariableDefine.directive_video, new VideoDirective());

			// 没有设置id参数的话则从数据模型里面取栏目id
			TemplateModel idTemplateModel = env.getDataModel().get(VariableDefine.variable_cid);
			if (idTemplateModel != null) {
				variables.put(VariableDefine.variable_cid, idTemplateModel);
			}
			TemplateModel pidTemplateModel = env.getDataModel().get(VariableDefine.variable_pid);
			if (pidTemplateModel != null) {
				variables.put(VariableDefine.variable_pid, pidTemplateModel);
			}
			TemplateModel nameTemplateModel = env.getDataModel().get(VariableDefine.variable_cname);
			if (nameTemplateModel != null) {
				variables.put(VariableDefine.variable_cname, nameTemplateModel);
			}
			TemplateModel linkTemplateModel = env.getDataModel().get(VariableDefine.variable_clink);
			if (linkTemplateModel != null) {
				variables.put(VariableDefine.variable_clink, linkTemplateModel);
			}

			String content = FreemarkerUtils.process(snippetContent, variables, null);
			env.getOut().write(content);
		}
	}
}
