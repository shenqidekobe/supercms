package com.dw.suppercms.infrastructure.web.plugin;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.plugin.PlugInManageService;
import com.dw.suppercms.domain.plugin.PlugInInfo;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 插件管理
 * */
@RestController
@RequestMapping("/settings")
public class PlugInController extends BaseController{
	
	@Resource
	private PlugInManageService plugInManageService;
	
	/**
	 * 验证插件标记是否唯一
	 * */
	@RequestMapping(value = "/plugins/validateFiledName", method = { RequestMethod.GET })
	public boolean validateFieldName(Long id, String fieldName) {
		boolean valid = true;
		if (id == null) {
			valid = plugInManageService.validateFiledName(fieldName);
		} else {
			PlugInInfo PlugInInfo = plugInManageService.findPlugInById(id);
			if (!PlugInInfo.getFieldName().equals(fieldName)) {
				valid = plugInManageService.validateFiledName(fieldName);
			}
		}
		return valid;
	}

	/**
	 * 获取所有插件信息
	 * */
	@RequestMapping(value = "/plugins", method = { RequestMethod.GET })
	public List<PlugInInfo> all() {
		return plugInManageService.all();
	}

	/**
	 * 插件列表查询
	 * */
	@RequestMapping(value = "/plugins", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, int start, int length) {
		SearchResult<PlugInInfo> data = plugInManageService.paginateAll(start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	/**
	 * 获取单个插件信息
	 * */
	@RequestMapping(value = "/plugins/{id}", method = { RequestMethod.GET })
	public PlugInInfo id(@PathVariable Long id) {
		return plugInManageService.findPlugInById(id);
	}

	/**
	 * 创建插件
	 * */
	@RequestMapping(value = "/plugins", method = { RequestMethod.POST })
	@SystemLog(operation="创建插件",operType=OPER_TYPE.create)
	public PlugInInfo create(@RequestBody @Valid PlugInInfo plugin) {
		PlugInInfo newPlugInInfo = plugInManageService.create(plugin);
		requestBodyAsLog(newPlugInInfo);
		return newPlugInInfo;
	}

	/**
	 * 编辑插件
	 * */
	@RequestMapping(value = "/plugins/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑插件",operType=OPER_TYPE.save)
	public PlugInInfo save(@RequestBody @Valid PlugInInfo plugin, BindingResult br) {
		plugInManageService.update(plugin.getId(), plugin);
		requestBodyAsLog(plugin);
		return plugin;
	}

	/**
	 * 删除插件
	 * */
	@RequestMapping(value = "/plugins/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除插件",operType=OPER_TYPE.remove)
	public void remove(@PathVariable Long id) {
		plugInManageService.delete(id);
	}

}
