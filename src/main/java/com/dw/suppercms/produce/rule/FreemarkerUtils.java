package com.dw.suppercms.produce.rule;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.data.DataRecordTransformConvetor;
import com.dw.suppercms.produce.listener.event.MakeFileTask;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Utils - Freemarker
 * 
 * @author kobe
 */
public final class FreemarkerUtils {

	private FreemarkerUtils() {
	}


	/**
	 * 解析字符串模板
	 * 
	 * @param template
	 *            字符串模板
	 * @param model
	 *            数据
	 * @param configuration
	 *            配置
	 * @return 解析后内容
	 */
	public static String process(String template, Map<String, ?> model, Configuration configuration) throws IOException, TemplateException {
		if (template == null) {
			return null;
		}
		if (configuration == null) {
			configuration = new Configuration();
			configuration.setClassicCompatible(true);
			configuration.setDefaultEncoding("UTF-8");
			StringTemplateLoader loader = new StringTemplateLoader();
			configuration.setTemplateLoader(loader);
		}
		StringWriter out = new StringWriter();
		new Template("template", new StringReader(template), configuration).process(model, out);
		return out.toString();
	}

	/**
	 * 获取变量
	 * 
	 * @param name
	 *            名称
	 * @param env
	 *            Environment
	 * @return 变量
	 */
	public static TemplateModel getVariable(String name, Environment env) throws TemplateModelException {
		Assert.hasText(name);
		Assert.notNull(env);
		return env.getVariable(name);
	}

	/**
	 * 设置变量
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            变量值
	 * @param env
	 *            Environment
	 */
	public static void setVariable(String name, Object value, Environment env) throws TemplateException {
		Assert.hasText(name);
		Assert.notNull(env);
		if (value instanceof TemplateModel) {
			env.setVariable(name, (TemplateModel) value);
		} else {
			env.setVariable(name, ObjectWrapper.BEANS_WRAPPER.wrap(value));
		}
	}

	/**
	 * 设置变量
	 * 
	 * @param variables
	 *            变量
	 * @param env
	 *            Environment
	 */
	public static void setVariables(Map<String, Object> variables, Environment env) throws TemplateException {
		Assert.notNull(variables);
		Assert.notNull(env);
		for (Entry<String, Object> entry : variables.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof TemplateModel) {
				env.setVariable(name, (TemplateModel) value);
			} else {
				env.setVariable(name, ObjectWrapper.BEANS_WRAPPER.wrap(value));
			}
		}
	}
	
	/**
	 * 解析最终页模版
	 * @param task
	 * @return Writer
	 * */
	public static Writer parseContentTemplate(MakeFileTask task,SimpleHash simpleHash,Configuration configuration){
		DataRecord dataRecord=task.getDataRecord();
		Object data=DataRecordTransformConvetor.dataRecordTransformDynaBean(dataRecord,task.getColumn());
		simpleHash.put(VariableDefine.variable_obj, data);
		simpleHash.put(VariableDefine.variable_cid, task.getFileId());
		simpleHash.put(VariableDefine.variable_sid, task.getWebId());
		simpleHash.put(VariableDefine.variable_cname, task.getSourceName());
		simpleHash.put(VariableDefine.variable_pid, task.getParentId());
		simpleHash.put(VariableDefine.variable_clink, task.getColumn().getFileWebpath(1));

		Writer output=new StringWriter();
		try {
			Template template = configuration.getTemplate(VariableDefine.template_prefix_templateinfo+task.getTemplateId());
			template.process(simpleHash, output);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		}
		return output;
	}
	
}