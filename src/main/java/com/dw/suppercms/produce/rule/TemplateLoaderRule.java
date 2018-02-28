package com.dw.suppercms.produce.rule;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.templates.TemplateInfo;

import freemarker.cache.TemplateLoader;

/**
 * 定义加载模版的规则
 * */
@Service
public class TemplateLoaderRule implements TemplateLoader{
	
	@Resource
	private ProduceFileService produceFileService;

	/**
	 * 根据名称获取指定的模版资源
	 * */
	@Override
	public Object findTemplateSource(String name) throws IOException {
		String names = name.split("_")[0];
		String[] split = names.split("#");
		String type = split[0];
		Long id = Long.parseLong(split[1]);

		Object templateSource = null;
		if (type.equals(TemplateInfo.class.getSimpleName())) {
			templateSource = produceFileService.findTemplateInfoById(id);
		} else if (type.equals(Custom.class.getSimpleName())) {
			templateSource = produceFileService.findCustomById(id);
		}
		return templateSource;
	}

	/**
	 * 返回模版资源最后一次修改的时间
	 * */
	@Override
	public long getLastModified(Object templateSource) {
		long lastModifyDate=0;
		try {
			if(templateSource instanceof TemplateInfo){
				lastModifyDate=((TemplateInfo)templateSource).getUpdateTime().getTime();
			}else if(templateSource instanceof Custom){
				lastModifyDate=((Custom)templateSource).getUpdateTime().getTime();
			}
		} catch (Exception e) {
		}
		return lastModifyDate;
	}

	/**
	 * 返回读取模版资源的reader
	 * */
	@Override
	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		StringReader stringReader=null;
		if(templateSource instanceof TemplateInfo){
			stringReader=new StringReader(((TemplateInfo)templateSource).getTemplateCode());
		}else if(templateSource instanceof Custom){
			stringReader=new StringReader(((Custom)templateSource).getTemplateCode());
		}
		return stringReader;
	}

	/**
	 * 关闭模版资源
	 * */
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
	}

}
