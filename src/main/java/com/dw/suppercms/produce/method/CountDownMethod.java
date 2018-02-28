package com.dw.suppercms.produce.method;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import freemarker.ext.beans.DateModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 模板方法 - 倒计时
 */
@Service
public class CountDownMethod implements TemplateMethodModelEx {

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List paramList) throws TemplateModelException {
		if (!CollectionUtils.isEmpty(paramList)) {
			TemplateDateModel dateModel = (DateModel)paramList.get(0);
			Date endDate = dateModel.getAsDate();
			Date currentDate = new Date();
			if (endDate.after(currentDate)) {
				return (endDate.getTime() - currentDate.getTime())/1000;
			}
		}
		return 0;
	}

}
