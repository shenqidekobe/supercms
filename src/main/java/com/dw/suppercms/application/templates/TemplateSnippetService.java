package com.dw.suppercms.application.templates;

import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.search.SearchResult;

public interface TemplateSnippetService {

	/**
	 * 检索模版片段列表
	 * @param sortId
	 * @param pager
	 * */
	SearchResult<TemplateSnippet> findTemplateSnippetList(Long sortId,Pager pager);
	
	/**
	 * 创建模版片段
	 * */
	boolean createTemplateSnippet(TemplateSnippet snippet);
	
	/**
	 * 修改模版片段
	 * */
	boolean modifyTemplateSnippet(Long id,TemplateSnippet snippet);
	
	/**
	 * 检索模版片段按ID
	 * */
	TemplateSnippet findTemplateSnippetById(Long id);
	
	/**
	 * 删除模版片段按ID
	 * */
	boolean removeTemplateSnippet(Long id);
	
	/**
	 * 验证片段TAG是否存在
	 * */
	boolean findTemplateSnippetTagIsExists(String snippetTag);

}
