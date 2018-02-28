package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 统计指令
 * 
 * <@stat condition="type=0" />
 * 
 * @author osmos
 * 
 */
@Service
@SuppressWarnings("rawtypes")
public class StatDirective extends BaseDirective {


	public StatDirective() {
	}

	@Override
	public void execute(Environment env, Map params, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
		// condition参数
		Map<String, Object> conditonMap = new HashMap<String, Object>();
		Object conditionParam = params.get("condition");
		conditonMap = new HashMap<String, Object>();
		if (conditionParam == null) {
			throw new TemplateException("stat指令未指定condition参数", env);
		} else {
			conditonMap = new HashMap<String, Object>();
			String conditionStr = conditionParam.toString();
			String[] groups = conditionStr.split(",");
			for (String group : groups) {
				String key = group.split("=")[0];
				String value = group.split("=")[1];
				conditonMap.put(key, value);
			}
		}

	}

}
