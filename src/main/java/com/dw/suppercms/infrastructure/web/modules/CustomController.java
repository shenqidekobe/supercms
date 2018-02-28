package com.dw.suppercms.infrastructure.web.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.modules.CustomService;
import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
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
 * CustomController
 *
 * @author osmos
 * @date 2015年7月14日
 */
@RestController
@RequestMapping("/modules")
public class CustomController extends BaseController{

	@Autowired
	private CustomService customService;
	@Autowired
	private SysSortService sortService;

	// validate title
	@RequestMapping(value = "/customs/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = customService.validateTitle(title);
		} else {
			Custom custom = customService.retrieve(id);
			if (!custom.getTitle().equals(title)) {
				valid = customService.validateTitle(title);
			}
		}
		return valid;
	}

	// validate location
	@RequestMapping(value = "/customs/validateLocation", method = { RequestMethod.GET })
	public boolean validateLocation(Long id, String dirName) {
		boolean valid = true;
		dirName = StringUtils.prependIfMissing(dirName, "/", "/");
		if (id == null) {
			valid = customService.validateLocation(dirName);
		} else {
			Custom custom = customService.retrieve(id);
			if (!custom.getDirName().equals(dirName)) {
				valid = customService.validateLocation(dirName);
			}
		}
		return valid;
	}

	// retrieve all customs represent as data table
	@RequestMapping(value = "/customs", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long sortId, int start, int length) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("sortId", sortId);
		SearchResult<Custom> data = customService.paginateAll(condition, start, length,
				Lists.newArrayList(new Sort("location")));
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	@RequestMapping(value = "/customs", method = { RequestMethod.GET }, params = { "sortsTree" })
	public List<Combotree> sortstree() {
		List<SysSortInfo> sorts = sortService.findSortsByType(SORT_TYPE.template_custom);
		return TransformUtils.transformToSortCombotree(sorts);
	}

	// retrieve a custom by id
	@RequestMapping(value = "/customs/{id}", method = { RequestMethod.GET })
	public Custom id(@PathVariable Long id) {
		return customService.retrieve(id);
	}

	// create custom
	@RequestMapping(value = "/customs", method = { RequestMethod.POST })
	@SystemLog(operation="创建自定义页",operType=OPER_TYPE.create)
	@Description("创建自定义")
	@RequiresPermissions({ "app.modules.custom.create" })
	public void create(@RequestBody @Valid Custom custom) {
		customService.create(custom);
		requestBodyAsLog(custom);
	}

	// save custom
	@RequestMapping(value = "/customs/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑自定义页",operType=OPER_TYPE.save)
	@Description("修改自定义")
	@RequiresPermissions({ "app.modules.custom.save" })
	public Custom save(@RequestBody @Valid Custom custom, BindingResult br) {
		customService.update(custom.getId(), custom);
		requestBodyAsLog(custom);
		return custom;
	}

	// remove custom
	@RequestMapping(value = "/customs/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除自定义页",operType=OPER_TYPE.remove)
	@Description("创建自定义")
	@RequiresPermissions({ "app.modules.custom.remove" })
	public void remove(@PathVariable Long id) {
		customService.delete(id);
	}
}
