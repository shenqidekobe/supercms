package com.dw.suppercms.infrastructure.web.templates;

import org.apache.shiro.authz.annotation.Logical;
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
import com.dw.suppercms.application.templates.TemplateService;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 模版控制器
 * */
@RestController
@RequestMapping(value = "templates")
public class TemplateController extends BaseController {

	@Autowired
	private TemplateService templateService;

	@Autowired
	private SysSortService sysSortService;

	/**
	 * 模版列表
	 * */
	@ResponseBody
	@RequestMapping(value = "/template/list", method = { RequestMethod.GET })
	public String list(Long sortId, String templateType, Pager pager) {
		SearchResult<TemplateInfo> sr = this.templateService.findTemplateList(sortId, templateType, pager);
		String json = JSONSerializer().deepSerialize(sr);
		return json;
	}

	/**
	 * 模版类型列表
	 * */
	@ResponseBody
	@RequestMapping(value = "/template/findTemplateType", method = { RequestMethod.GET })
	public String findTemplateType(String templateType) {
		Pager pager = new Pager(0, Integer.MAX_VALUE);
		SearchResult<TemplateInfo> sr = this.templateService.findTemplateList(null, templateType, pager);
		String json = JSONSerializer().deepSerialize(sr.getResult());
		return json;
	}

	/**
	 * 创建
	 * */
	@ResponseBody
	@RequestMapping(value = "/template", method = { RequestMethod.POST })
	@SystemLog(operation = "创建模版", operType = OPER_TYPE.create)
	@Description("创建模板")
	@RequiresPermissions(logical = Logical.OR, value = { "app.template.index.create", "app.template.list.create",
			"app.template.content.create", "app.template.custom.create" })
	public String create(@RequestBody TemplateInfo template) {
		if (template.getSortId() != null) {
			template.setSort(sysSortService.findSortById(template.getSortId()));
		}
		boolean flag = this.templateService.createTemplate(template);
		requestBodyAsLog(template);
		return String.valueOf(flag);
	}

	/**
	 * 更新模版
	 * */
	@ResponseBody
	@RequestMapping(value = "/template/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑模版", operType = OPER_TYPE.save)
	@Description("修改模板")
	@RequiresPermissions(logical = Logical.OR, value = { "app.template.index.save", "app.template.list.save",
			"app.template.content.save", "app.template.custom.save" })
	public String save(@RequestBody TemplateInfo template) {
		if (template.getSortId() != null) {
			template.setSort(sysSortService.findSortById(template.getSortId()));
		}
		boolean flag = this.templateService.modifyTemplate(template.getId(), template);
		requestBodyAsLog(template);
		return String.valueOf(flag);
	}

	/**
	 * 查询单个模版信息
	 * */
	@ResponseBody
	@RequestMapping(value = "/template/{id}", method = { RequestMethod.GET })
	public String find(@PathVariable Long id) {
		TemplateInfo obj = this.templateService.findTemplateById(id);
		String json = JSONSerializer().serialize(obj);
		return json;
	}

	/**
	 * 删除单个模版信息
	 * */
	@ResponseBody
	@RequestMapping(value = "/template/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation = "删除模版", operType = OPER_TYPE.remove)
	@Description("删除模板")
	@RequiresPermissions(logical = Logical.OR, value = { "app.template.index.remove", "app.template.list.remove",
			"app.template.content.remove", "app.template.custom.remove" })
	public String remove(@PathVariable Long id) {
		boolean flag = this.templateService.removeTemplate(id);
		return String.valueOf(flag);
	}

}
