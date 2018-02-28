package com.dw.suppercms.infrastructure.web.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.application.templates.TemplateSnippetService;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 模版片段控制器
 * */
@RestController
@RequestMapping("/templates")
public class TemplateSnippetController extends BaseController{
	
	@Autowired
	private TemplateSnippetService templateSnippetService;
	
	@Autowired
	private SysSortService sysSortService;
	
	/**
	 * 片段列表
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet/list",method={RequestMethod.GET})
	public String list(Long sortId,Pager pager){
		SearchResult<TemplateSnippet> sr=this.templateSnippetService.findTemplateSnippetList(sortId,pager);
		String json=JSONSerializer().deepSerialize(sr);
		return json;
	}
	  
	/**
	 * 创建模版片段
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet",method={RequestMethod.POST})
	@SystemLog(operation="创建模版片段",operType=OPER_TYPE.create)
	@Description("创建片段")
	@RequiresPermissions({ "app.template.snippet.create" })
	public String create(@RequestBody TemplateSnippet snippet){
		if(snippet.getSortId()!=null){
			snippet.setSort(sysSortService.findSortById(snippet.getSortId()));
		}
		boolean	flag=this.templateSnippetService.createTemplateSnippet(snippet);
		requestBodyAsLog(snippet);
		return String.valueOf(flag);
	}
	
	/**
	 * 更新模版片段
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet/{id}",method={RequestMethod.PUT})
	@SystemLog(operation="编辑模版片段",operType=OPER_TYPE.save)
	@Description("修改片段")
	@RequiresPermissions({ "app.template.snippet.save" })
	public String save(@RequestBody TemplateSnippet snippet){
		if(snippet.getSortId()!=null){
			snippet.setSort(sysSortService.findSortById(snippet.getSortId()));
		}
		boolean	flag=this.templateSnippetService.modifyTemplateSnippet(snippet.getId(), snippet);
		requestBodyAsLog(snippet);
		return String.valueOf(flag);
	}
	
	
	/**
	 * 查询单个片段信息
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet/{id}",method={RequestMethod.GET})
	public String find(@PathVariable Long id){
		TemplateSnippet snippet=this.templateSnippetService.findTemplateSnippetById(id);
		return JSONSerializer().serialize(snippet);
	}
	
	/**
	 * 删除单个片段信息
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet/{id}",method={RequestMethod.DELETE})
	@SystemLog(operation="删除模版片段",operType=OPER_TYPE.remove)
	@Description("删除片段")
	@RequiresPermissions({ "app.template.snippet.remove" })
	public String remove(@RequestBody Long id){
		boolean flag=this.templateSnippetService.removeTemplateSnippet(id);
		return String.valueOf(flag);
	}
	
	/**
	 * 验证snippetTag字段的唯一性
	 * @param id:表示修改片段时验证属性
	 * */
	@ResponseBody
	@RequestMapping(value="/snippet/verify",method={RequestMethod.GET})
	public String verifyUnique(String snippetTag,Long id){
		boolean isVerify=true;
		if(id!=null){
			TemplateSnippet snippet=templateSnippetService.findTemplateSnippetById(id);
			if(snippet.getSnippetTag().equals(snippetTag)){
				isVerify=false;
			}
		}
		boolean tagFlag=isVerify?this.templateSnippetService.findTemplateSnippetTagIsExists(snippetTag):false;
		boolean flag=!tagFlag;//存在就返回false，提示页面验证失败
		Map<String,Boolean> map=new HashMap<String,Boolean>();
		map.put("valid", flag);
		return JSONSerializer().deepSerialize(map);
	}
	
}
