package com.dw.suppercms.application.templates.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.templates.TemplateSnippetService;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.domain.templates.TemplateSnippetRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 模版片段服务
 * */
@ApplicationService
public class TemplateSnippetServiceImpl implements TemplateSnippetService{
	
	@Autowired
	private TemplateSnippetRepository templateSnippet;
	
	@Autowired
	private SysSortRepository sysSort;

	public SearchResult<TemplateSnippet> findTemplateSnippetList(Long sortId,Pager pager) {
		Search search=new Search();
		if(sortId!=null){
			search.addFilterEqual("sort", sysSort.find(sortId));
		}
		search.setFirstResult(pager.getStartIndex());
		search.setMaxResults(pager.getPageSize());
		String sort = pager.getSort();
		if (StringUtils.isNotEmpty(sort)) {
			search.addSort(sort, StringUtils.equalsIgnoreCase(pager.getDir(), "desc") ? true : false);
		} else {
			search.addSort("createTime", true);
		}
		return this.templateSnippet.searchAndCount(search);
	}

	public boolean createTemplateSnippet(TemplateSnippet snippet) {
		return this.templateSnippet.save(snippet);
	}

	public boolean modifyTemplateSnippet(Long id, TemplateSnippet snippet) {
		TemplateSnippet obj=this.findTemplateSnippetById(id);
		obj.setSnippetName(snippet.getSnippetName());
		obj.setSnippetTag(snippet.getSnippetTag());
		obj.setSnippetCode(snippet.getSnippetCode());
		return this.templateSnippet.save(obj);
	}

	public TemplateSnippet findTemplateSnippetById(Long id) {
		return this.templateSnippet.find(id);
	}

	public boolean removeTemplateSnippet(Long id) {
		return this.templateSnippet.removeById(id);
	}

	public boolean findTemplateSnippetTagIsExists(String snippetTag) {
		Search search=new Search();
		search.addFilterEqual("snippetTag", snippetTag);
		return this.templateSnippet.count(search)>0?true:false;
	}

}
