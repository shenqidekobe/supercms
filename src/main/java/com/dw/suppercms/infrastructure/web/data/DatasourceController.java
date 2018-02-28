package com.dw.suppercms.infrastructure.web.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.data.DatasourceService;
import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Combotree;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.infrastructure.web.ui.TransformUtils;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * DatasourceController
 *
 * @author osmos
 * @date 2015年7月30日
 */
@RestController
@RequestMapping("/datas")
public class DatasourceController extends BaseController {

	@Autowired
	private DatasourceService datasourceService;
	@Autowired
	private SysSortService sortService;

	// validate title
	@RequestMapping(value = "/datasources/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = datasourceService.validateTitle(title);
		} else {
			Datasource datasource = datasourceService.retrieve(id);
			if (!datasource.getTitle().equals(title)) {
				valid = datasourceService.validateTitle(title);
			}
		}
		return valid;
	}

	// retrieve all datasources as list
	@RequestMapping(value = "/datasources", method = { RequestMethod.GET })
	public String all() {
		List<Datasource> all =  datasourceService.all();
		String json = new flexjson.JSONSerializer()
		.include("sort.id")
		.exclude("*.class", "model", "sort.*")
		.serialize(all);
		return json;
	}

	// retrieve datasources of user
	@RequestMapping(value = "/datasources", method = { RequestMethod.GET },params = { "own" })
	public List<Datasource> own() {
		return datasourceService.retrieveDatasources(((User)SecurityUtils.getSubject().getPrincipal()).getId());
	}

	// retrieve all datasources as datatable
	@RequestMapping(value = "/datasources", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long sortId, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("sortId", sortId);
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		conditon.put("sorts", Lists.newArrayList(new Sort("createTime", true)));
		SearchResult<Datasource> data = datasourceService.paginateAll(conditon);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	@RequestMapping(value = "/datasources", method = { RequestMethod.GET }, params = { "sortsTree" })
	public List<Combotree> sortstree() {
		List<SysSortInfo> sorts = sortService.findSortsByType(SORT_TYPE.datasource);
		return TransformUtils.transformToSortCombotree(sorts);
	}

	// retreive a datasource by id
	@RequestMapping(value = "/datasources/{id}", method = { RequestMethod.GET })
	public Datasource id(@PathVariable Long id) {
		return datasourceService.retrieve(id);
	}

	// create datasource
	@RequestMapping(value = "/datasources", method = { RequestMethod.POST })
	@SystemLog(operation = "创建数据源", operType = OPER_TYPE.create)
	@Description("创建数据源")
	@RequiresPermissions({ "app.datas.datasource.create" })
	public Datasource create(@RequestBody @Valid Datasource datasource) {
		Datasource newDatasource = datasourceService.create(datasource);
		requestBodyAsLog(newDatasource.getTitle());
		return newDatasource;
	}

	// save datasource
	@RequestMapping(value = "/datasources/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑数据源", operType = OPER_TYPE.save)
	@Description("修改数据源")
	@RequiresPermissions({ "app.datas.datasource.save" })
	public Datasource save(@RequestBody @Valid Datasource datasource, BindingResult br) {
		datasourceService.update(datasource.getId(), datasource);
		requestBodyAsLog(datasource.getTitle());
		return datasource;
	}

	// remove datasource
	@RequestMapping(value = "/datasources/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation = "删除数据源", operType = OPER_TYPE.remove)
	@Description("删除数据源")
	@RequiresPermissions({ "app.datas.datasource.remove" })
	public void remove(@PathVariable Long id) {
		datasourceService.delete(id);
	}
}
