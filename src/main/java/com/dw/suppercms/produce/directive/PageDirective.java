package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dw.suppercms.produce.rule.VariableDefine;

import freemarker.core.Environment;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 分页指令
 * 
 */
@Service
public class PageDirective extends BaseDirective {
	
	private final static String styleParamName="style";

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] models, TemplateDirectiveBody body) throws TemplateException, IOException {
		Object styleParam = params.get(styleParamName);
		try {
			
			TemplateModel firstTM = env.getVariable(VariableDefine.pading_first);
			TemplateModel prevTM = env.getVariable(VariableDefine.pading_prev);
			TemplateModel nextTM = env.getVariable(VariableDefine.pading_next);
			TemplateModel lastTM = env.getVariable(VariableDefine.pading_last);
			TemplateModel pagethTM = env.getVariable(VariableDefine.pading_pageth);
			Integer pageCount = Integer.valueOf(env.getVariable(VariableDefine.pading_pageCount).toString());
			
			
			/*log.debug("pageDirective：coid=" + env.getVariable("coid").toString() + "||pageth=" + pagethTM);
			log.debug("pageCount:" + pageCount);
			log.debug("firstTM:" + firstTM);
			log.debug("prevTM:" + prevTM);
			log.debug("pagethTM:" + pagethTM);
			log.debug("nextTM:" + nextTM);
			log.debug("lastTM:" + lastTM);*/
			CollectionModel pagethsHrefListTM = (CollectionModel)env.getVariable(VariableDefine.pading_pagethsHrefList);
			CollectionModel pagethsNumListTM = (CollectionModel) env.getVariable(VariableDefine.pading_pagethNumList);
			if (pagethsNumListTM.size() == 0) {
				return;
			}
			String html = "";
			if (styleParam == null || styleParam.toString().equals("1")) {
				String pagelist = "";
				for (int i = 0; i < pagethsNumListTM.size(); i++) {
					Integer num = Integer.parseInt(pagethsNumListTM.get(i).toString());
					String href = pagethsHrefListTM.get(i).toString();

					if (i == (pagethsNumListTM.size() - 1) && pagethsNumListTM.size() > 1
							&& Integer.valueOf(pagethsNumListTM.get(pagethsNumListTM.size() - 2).toString()) != (pageCount - 1)) {
						pagelist += "．．";
					}

					if (num.toString().equals(pagethTM.toString())) {
						pagelist += "<a class='on' href='" + href + "'>" + num + "</a>";
					} else {
						pagelist += "<a href='" + href + "'>" + num + "</a>";
					}
					if (i == 0 && pagethsNumListTM.size() > 2 && Integer.valueOf(pagethsNumListTM.get(1).toString()) != 2) {
						pagelist += "．．";
					}
				}
				html = "<div>" + "<a href='" + prevTM.toString() + "'>&lt;</a>" + pagelist + "<a href='" + nextTM.toString() + "'>&gt;</a>" + "</div>";

			} else if (styleParam.toString().equals("2")) {
				html = "<div>" + "<a href='" + firstTM.toString() + "'>首页</a>" + "<a href='" + prevTM.toString() + "'>上页</a>" + "<a href='" + nextTM.toString()
						+ "'>下页</a>" + "<a href='" + lastTM.toString() + "'>尾页</a>" + "</div>";
			}

			env.getOut().write(html);
			if (body == null) {
				body = new TemplateDirectiveBody() {
					public void render(Writer writer) throws TemplateException, IOException {}
				};
			}
			body.render(env.getOut());
		} catch (Exception e) {
			String desc=String.format("分页指令解析错误,style=%s", styleParam);
			saveDirectiveParseErrorLog(desc, e.getMessage());
		}
		
	}
}
