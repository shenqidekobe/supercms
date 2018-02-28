package com.dw.suppercms.infrastructure.web.system;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Combotree;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.infrastructure.web.ui.TransformUtils;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 分类处理器
 * */
@RestController
@RequestMapping("systems")
public class SysSortController extends BaseController{
	
	@Autowired
	private SysSortService sysSortService;
	

	/**
	 * 验证分类名称是否唯一
	 * */
	@RequestMapping(value = "/sort/validateSortName", method = { RequestMethod.GET })
	public boolean validateSortName(Long id, String sortType,String sortName) {
		boolean valid = true;
		if (id == null) {
			valid = !this.sysSortService.findSortNameIsExists(sortName, sortType);
		} else {
			SysSortInfo sort = sysSortService.findSortById(id);
			if (!sort.getSortName().equals(sortName)) {
				valid = !this.sysSortService.findSortNameIsExists(sortName, sortType);
			}
		}
		return valid;
	}
	
	// retrieve all columns represent as data table
	@RequestMapping(value = "/sort", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(String sortType, int draw,int start, int length) {
		SORT_TYPE type=StringUtils.isEmpty(sortType)?null:SORT_TYPE.valueOf(SORT_TYPE.class,sortType);
		SearchResult<SysSortInfo> data = sysSortService.findSortList(type, start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}
	
	/**
	 * 获取树tree
	 * */
	@RequestMapping(value = "/sort", method = { RequestMethod.GET }, params = { "sortsTree" })
	public List<Combotree> sortstree(String sortType) {
		SORT_TYPE type=StringUtils.isEmpty(sortType)?null:SORT_TYPE.valueOf(SORT_TYPE.class,sortType);
		List<SysSortInfo> sorts = sysSortService.findSortsByType(type);
		return TransformUtils.transformToSortCombotree(sorts);
	}
	
	/**
	 * 分类树
	 * */
	@ResponseBody
	@RequestMapping(value="/sort",method={RequestMethod.GET},params={"sortTree"})
	public String sortTree(String type){
		List<SysSortInfo> list=this.sysSortService.findRootSortByType(type);
		String json= JSONSerializer().deepSerialize(list);
		return json;
	}
	
	/**
	 * 找回某类型的所有分类
	 * */
	@ResponseBody
	@RequestMapping(value="/sort",method={RequestMethod.GET},params={"byType"})
	public String byType(SORT_TYPE type){
		List<SysSortInfo> list=this.sysSortService.findSortsByType(type);
		String json= JSONSerializer().include("id", "sortName", "parentId").exclude("*.class", "*").serialize(list);
		return json;
	}
	
	/**
	 * 创建分类
	 * */
	@SystemLog(operation="创建分类",operType=OPER_TYPE.create)
	@ResponseBody
	@RequestMapping(value="/sort",method={RequestMethod.POST})
	@Description("创建分类")
	@RequiresPermissions({ "app.settings.sort.create" })
	public String create(@RequestBody SysSortInfo sort){
		boolean flag=this.sysSortService.createSysSort(sort.getParentId(), sort.getSortType().toString(), sort.getSortName());
		requestBodyAsLog(sort);
		return String.valueOf(flag);
	}
	
	/**
	 * 创建分类
	 * */
	@ResponseBody
	@RequestMapping(value="/sort/{id}",method={RequestMethod.PUT})
	@SystemLog(operation="编辑分类",operType=OPER_TYPE.save)
	@Description("修改分类")
	@RequiresPermissions({ "app.settings.sort.save" })
	public String save(@RequestBody SysSortInfo sort){
		boolean flag=this.sysSortService.modifySysSort(sort.getId(), sort);
		requestBodyAsLog(sort);
		return String.valueOf(flag);
	}
	
	/**
	 * 删除分类
	 * */
	@ResponseBody
	@RequestMapping(value="/sort/{id}",method={RequestMethod.DELETE})
	@SystemLog(operation="删除分类",operType=OPER_TYPE.remove)
	@Description("删除分类")
	@RequiresPermissions({ "app.settings.sort.remove" })
	public String remove(@PathVariable Long id){
		boolean flag=this.sysSortService.removeSysSort(id);
		
		return String.valueOf(flag);
	}
	
	/**
	 * 获取分类
	 * */
	@ResponseBody
	@RequestMapping(value="/sort/{id}",method={RequestMethod.GET})
	public SysSortInfo get(@PathVariable Long id){
		return this.sysSortService.findSortById(id);
	}
	
	/**
	 * 验证分类名称重复性
	 * */
	@ResponseBody
	@RequestMapping(value="/sort/verify",method={RequestMethod.GET})
	public String verifyUnique(String sortName,String sortType,Long id){
		boolean isVerify=true;
		if(id!=null){
			SysSortInfo sort=sysSortService.findSortById(id);
			if(sort.getSortName().equals(sortName)){
				isVerify=false;
			}
		}
		boolean tagFlag=isVerify?this.sysSortService.findSortNameIsExists(sortName, sortType):false;
		return String.valueOf(!tagFlag);
	}

}
