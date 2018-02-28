package com.dw.suppercms.infrastructure.web.system;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.SysParamService;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.SysParamInfo;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;

/**
 * 系统参数
 * */
@RestController
@RequestMapping("/systems")
public class SysParamController extends BaseController{
	
	@Resource
	private SysParamService sysParamService;
	
	/**
	 * 验证key是否唯一
	 * */
	@RequestMapping(value = "/params/validateTaskName", method = { RequestMethod.GET })
	public boolean validateTaskKey(Long id, String paramCode,String paramKey) {
		boolean valid = true;
		PARAM_CODE code=StringUtils.isEmpty(paramCode)?null:PARAM_CODE.valueOf(PARAM_CODE.class,paramCode);
		if (id == null) {
			valid = sysParamService.validateParamKey(code, paramKey);
		} else {
			SysParamInfo param = sysParamService.findSysParamById(id);
			if (!param.getParamKey().equals(paramKey)) {
				valid = sysParamService.validateParamKey(code, paramKey);
			}
		}
		return valid;
	}
	

	/**
	 * 获取所有任务信息
	 * */
	@RequestMapping(value = "/params", method = { RequestMethod.GET })
	public List<SysParamInfo> all() {
		return sysParamService.all();
	}
	
	/**
	 * 获取信息
	 * */
	@RequestMapping(value = "/params/{id}", method = { RequestMethod.GET })
	public SysParamInfo id(@PathVariable Long id) {
		return sysParamService.findSysParamById(id);
	}
	
	/**
	 * 获取信息
	 * */
	@RequestMapping(value = "/params", method = { RequestMethod.GET },params={"find"})
	public SysParamInfo find(String paramCode,String paramKey) {
		PARAM_CODE code=StringUtils.isEmpty(paramCode)?null:PARAM_CODE.valueOf(PARAM_CODE.class,paramCode);
		return sysParamService.findSysParamByCodeAndKey(code, paramKey);
	}

	/**
	 * 创建任务
	 * */
	@RequestMapping(value = "/params", method = { RequestMethod.POST })
	@SystemLog(operation="创建系统参数",operType=OPER_TYPE.create)
	@Description("创建参数")
	@RequiresPermissions({ "app.settings.params.create" })
	public SysParamInfo create(@RequestBody @Valid SysParamInfo obj) {
		SysParamInfo newSysParamInfo = sysParamService.createSysParam(obj);
		requestBodyAsLog(newSysParamInfo);
		return newSysParamInfo;
	}

	/**
	 * 编辑
	 * */
	@RequestMapping(value = "/params/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑系统参数",operType=OPER_TYPE.save)
	@Description("修改参数")
	@RequiresPermissions({ "app.settings.params.save" })
	public SysParamInfo save(@RequestBody @Valid SysParamInfo obj, BindingResult br) {
		SysParamInfo newParam=sysParamService.modifySysParam(obj.getId(), obj);
		requestBodyAsLog(newParam);
		return newParam;
	}
	

	/**
	 * 删除
	 * */
	@RequestMapping(value = "/params/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除系统参数",operType=OPER_TYPE.remove)
	@Description("删除参数")
	@RequiresPermissions({ "app.settings.params.remove" })
	public void remove(@PathVariable Long id) {
		sysParamService.removeSysParam(id);
	}

}
