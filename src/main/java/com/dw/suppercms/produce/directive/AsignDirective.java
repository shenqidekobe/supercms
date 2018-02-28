package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Service
public class AsignDirective implements TemplateDirectiveModel {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Environment env,  Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Set<String> paramNames = params.keySet();
		for (String paramName : paramNames) {
			env.setVariable(paramName, env.getConfiguration().getObjectWrapper().wrap(params.get(paramName)));
		}
	}

}
