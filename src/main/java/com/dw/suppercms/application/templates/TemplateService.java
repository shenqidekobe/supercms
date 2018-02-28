package com.dw.suppercms.application.templates;

import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.search.SearchResult;

public interface TemplateService {
	
	/**
	 * 检索模版模版列表
	 * @param sortId
	 * @param pager
	 * */
	SearchResult<TemplateInfo> findTemplateList(Long sortId,String templateType,Pager pager);
	
	/**
	 * 创建模版
	 * */
	boolean createTemplate(TemplateInfo template);
	
	/**
	 * 修改模版
	 * */
	boolean modifyTemplate(Long id,TemplateInfo template);
	
	/**
	 * 检索模版按ID
	 * */
	TemplateInfo findTemplateById(Long id);
	
	/**
	 * 删除模版按ID
	 * */
	boolean removeTemplate(Long id);

}
