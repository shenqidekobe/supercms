package com.dw.suppercms.infrastructure.web.manuscript;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Description;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.manu.ManuscriptOrganService;
import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.dw.suppercms.domain.manu.ManuscriptOrgan.ORGAN_LEVEL;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 投稿单位处理
 * @author kobe
 * */
@RestController
@RequestMapping("/manuscripts")
public class OrganController extends BaseController{
	
	@Resource
	private ManuscriptOrganService manuscriptOrganService;
	
	/**
	 * 验证单位标记是否唯一
	 * */
	@RequestMapping(value = "/organs/validatePlatformOrganId", method = { RequestMethod.GET })
	public boolean validatePlatformOrganId(Long id, String platformOrganId) {
		boolean valid = true;
		if (id == null) {
			valid = manuscriptOrganService.validatePlatformOrganId(platformOrganId);
		} else {
			ManuscriptOrgan manuscriptOrgan = manuscriptOrganService.findOrganById(id);
			if (!manuscriptOrgan.getPlatformOrganId().equals(platformOrganId)) {
				valid = manuscriptOrganService.validatePlatformOrganId(platformOrganId);
			}
		}
		return valid;
	}

	/**
	 * 获取所有信息
	 * */
	@RequestMapping(value = "/organs", method = { RequestMethod.GET })
	public List<ManuscriptOrgan> all() {
		return manuscriptOrganService.all();
	}
	
	/**
	 * 获取单位等级信息
	 * */
	@RequestMapping(value = "/organs", method = { RequestMethod.GET },params={"level"})
	public ORGAN_LEVEL[] level() {
		return ORGAN_LEVEL.values();
	}

	/**
	 * 单位列表查询
	 * */
	@RequestMapping(value = "/organs", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, int start, int length) {
		SearchResult<ManuscriptOrgan> data = manuscriptOrganService.paginateAll(start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	/**
	 * 获取单个单位信息
	 * */
	@RequestMapping(value = "/organs/{id}", method = { RequestMethod.GET })
	public ManuscriptOrgan id(@PathVariable Long id) {
		return manuscriptOrganService.findOrganById(id);
	}

	/**
	 * 创建单位
	 * */
	@RequestMapping(value = "/organs", method = { RequestMethod.POST })
	@SystemLog(operation="创建投稿单位",operType=OPER_TYPE.create)
	@Description("创建单位")
	@RequiresPermissions({ "app.manuscripts.organ.create" })
	public ManuscriptOrgan create(@RequestBody @Valid ManuscriptOrgan organ) {
		ManuscriptOrgan newManuscriptOrgan = manuscriptOrganService.create(organ);
		requestBodyAsLog(newManuscriptOrgan);
		return newManuscriptOrgan;
	}

	/**
	 * 编辑单位
	 * */
	@RequestMapping(value = "/organs/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑投稿单位",operType=OPER_TYPE.save)
	@Description("修改单位")
	@RequiresPermissions({ "app.manuscripts.organ.save" })
	public ManuscriptOrgan save(@RequestBody @Valid ManuscriptOrgan organ, BindingResult br) {
		manuscriptOrganService.update(organ.getId(), organ);
		requestBodyAsLog(organ);
		return organ;
	}

	/**
	 * 删除单位
	 * */
	@RequestMapping(value = "/organs/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除投稿单位",operType=OPER_TYPE.remove)
	@Description("删除单位")
	@RequiresPermissions({ "app.manuscripts.organ.remove" })
	public void remove(@PathVariable Long id) {
		manuscriptOrganService.delete(id);
	}

}
