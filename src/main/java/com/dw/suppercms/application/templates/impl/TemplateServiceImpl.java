package com.dw.suppercms.application.templates.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.templates.TemplateService;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.dw.suppercms.domain.modules.CustomRepository;
import com.dw.suppercms.domain.modules.SiteRepository;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.domain.templates.TemplateRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 模版服务
 * */
@ApplicationService
public class TemplateServiceImpl implements TemplateService{
	
	@Autowired
	private TemplateRepository template;
	
	@Autowired
	private SysSortRepository sysSort;
	
	@Autowired
	private SiteRepository site;
	
	@Autowired
	private ColumnRepository column;
	
	@Autowired
	private CustomRepository custom;

	public SearchResult<TemplateInfo> findTemplateList(Long sortId,
			String templateType, Pager pager) {
		Search search=new Search();
		if(sortId!=null){
			search.addFilterEqual("sort", sysSort.find(sortId));
		}
		search.addFilterEqual("templateType", TemplateInfo.TEMPLATE_TYPE.valueOf(templateType));
		search.setFirstResult(pager.getStartIndex());
		search.setMaxResults(pager.getPageSize());
		String sort = pager.getSort();
		if (StringUtils.isNotEmpty(sort)) {
			search.addSort(sort, StringUtils.equalsIgnoreCase(pager.getDir(), "desc") ? true : false);
		} else {
			search.addSort("createTime", true);
		}
		return this.template.searchAndCount(search);
	}

	public boolean createTemplate(TemplateInfo template) {
		return this.template.save(template);
	}

	public boolean modifyTemplate(Long id, TemplateInfo template) {
		TemplateInfo obj=this.findTemplateById(id);
		obj.setTemplateName(template.getTemplateName());
		obj.setTemplateCode(template.getTemplateCode());
		obj.setSort(template.getSort());
		return this.template.save(obj);
	}

	public TemplateInfo findTemplateById(Long id) {
		return this.template.find(id);
	}

	public boolean removeTemplate(Long id) {
		TemplateInfo template=this.findTemplateById(id);
		switch (template.getTemplateType()) {
		case index:
			if(!site.search(new Search().addFilterEqual("homeTemplateId", id)).isEmpty())
				throw new BusinessException(String.format("当前模版：%s 正在被站点使用中，不能直接删除。",template.getTemplateName()));
			break;
		case list:
			if(!column.search(new Search().addFilterEqual("homeTemplateId", id)).isEmpty())
				throw new BusinessException(String.format("当前模版：%s 正在被栏目列表页使用中，不能直接删除。",template.getTemplateName()));
			break;
		case content:
			if(!column.search(new Search().addFilterEqual("contentTemplateId", id)).isEmpty())
				throw new BusinessException(String.format("当前模版：%s 正在被栏目内容页使用中，不能直接删除。",template.getTemplateName()));
			break;
		case custom:
			if(!custom.search(new Search().addFilterEqual("customTemplateId", id)).isEmpty())
				throw new BusinessException(String.format("当前模版：%s 正在被自定义页使用中，不能直接删除。",template.getTemplateName()));
			break;
		default:
			break;
		}
		return this.template.remove(template);
	}

}
