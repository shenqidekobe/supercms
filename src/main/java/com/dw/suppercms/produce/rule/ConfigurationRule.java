package com.dw.suppercms.produce.rule;

import javax.annotation.Resource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * 定义freemarker的Configuration实例创建规则
 * */
@Service
public class ConfigurationRule implements FactoryBean<Configuration>{
	
	private static final String ENCODING="UTF-8";
	
	private static final String DATEFORMAT="yyyy-MM-dd";
	
	private static final String DATETIMEFORMAT="yyyy-MM-dd HH:mm:ss";
	
	@Resource
	private TemplateLoaderRule templateLoaderRule;

	@Override
	public Configuration getObject() throws Exception {
		//Version version=new Version(Version.class.getPackage().getSpecificationVersion());
		Configuration configuration=new Configuration();
		BeansWrapper beansWrapper=new BeansWrapper();
		configuration.setTemplateLoader(templateLoaderRule);
		configuration.setDefaultEncoding(ENCODING);
		configuration.setDateFormat(DATEFORMAT);
		configuration.setDateTimeFormat(DATETIMEFORMAT);
		configuration.setClassicCompatible(true);
		configuration.setObjectWrapper(beansWrapper);//设置对象包装器
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);//设置异常处理器
		return configuration;
	}

	@Override
	public Class<?> getObjectType() {
		return Configuration.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	

}
