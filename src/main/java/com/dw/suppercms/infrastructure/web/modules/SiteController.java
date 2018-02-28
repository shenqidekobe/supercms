package com.dw.suppercms.infrastructure.web.modules;

import java.io.File;
import java.util.List;

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

import com.dw.suppercms.application.modules.SiteService;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.produce.rule.CmsFileUtils;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * SiteController
 *
 * @author osmos
 * @date 2015年6月26日
 */
@RestController
@RequestMapping("/modules")
public class SiteController extends BaseController{

	@Autowired
	private SiteService siteService;

	// validate title
	@RequestMapping(value = "/sites/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = siteService.validateTitle(title);
		} else {
			Site site = siteService.retrieve(id);
			if (!site.getTitle().equals(title)) {
				valid = siteService.validateTitle(title);
			}
		}
		return valid;
	}

	// validate test domain
	@RequestMapping(value = "/sites/validateTestDomain", method = { RequestMethod.GET })
	public boolean validateTestDomain(Long id, String testDomain) {
		boolean valid = true;
		if (id == null) {
			valid = siteService.validateTestDomain(testDomain);
		} else {
			Site site = siteService.retrieve(id);
			if (!site.getTestDomain().equals(testDomain)) {
				valid = siteService.validateTestDomain(testDomain);
			}
		}
		return valid;
	}

	// validate product domain
	@RequestMapping(value = "/sites/validateProductDomain", method = { RequestMethod.GET })
	public boolean validateProductDomain(Long id, String productDomain) {
		boolean valid = true;
		if (id == null) {
			valid = siteService.validateProductDomain(productDomain);
		} else {
			Site site = siteService.retrieve(id);
			if (!site.getProductDomain().equals(productDomain)) {
				valid = siteService.validateProductDomain(productDomain);
			}
		}
		return valid;
	}

	// validate direcotry
	@RequestMapping(value = "/sites/validateDirName", method = { RequestMethod.GET })
	public boolean validateDirName(Long id, String dirName) {
		boolean valid = true;
		dirName = StringUtils.prependIfMissing(dirName, "/", "/");
		if (id == null) {
			valid = siteService.validateDirName(dirName);
		} else {
			Site site = siteService.retrieve(id);
			if (!site.getDirName().equals(dirName)) {
				valid = siteService.validateDirName(dirName);
			}
		}
		return valid;
	}

	// retrieve all sites as list
	@RequestMapping(value = "/sites", method = { RequestMethod.GET })
	public List<Site> all() {
		return siteService.all();
	}

	// retrieve all sites as datatable
	@RequestMapping(value = "/sites", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, int start, int length) {
		SearchResult<Site> data = siteService.paginateAll(start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	// retreive a site by id
	@RequestMapping(value = "/sites/{id}", method = { RequestMethod.GET })
	public Site id(@PathVariable Long id) {
		return siteService.retrieve(id);
	}

	// create site
	@RequestMapping(value = "/sites", method = { RequestMethod.POST })
	@SystemLog(operation="创建站点",operType=OPER_TYPE.create)
	@Description("创建站点")
	@RequiresPermissions({ "app.modules.site.create" })
	public Site create(@RequestBody @Valid Site site) {
		Site newSite = siteService.create(site);
		requestBodyAsLog(newSite);
		return newSite;
	}

	// save site
	@RequestMapping(value = "/sites/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑站点",operType=OPER_TYPE.save)
	@Description("修改站点")
	@RequiresPermissions({ "app.modules.site.save" })
	public Site save(@RequestBody @Valid Site site, BindingResult br) {
		siteService.update(site.getId(), site);
		requestBodyAsLog(site);
		return site;
	}

	// remove site
	@RequestMapping(value = "/sites/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除站点",operType=OPER_TYPE.remove)
	@Description("删除站点")
	@RequiresPermissions({ "app.modules.site.remove" })
	public void remove(@PathVariable Long id) {
		siteService.delete(id);
	}
	
	// publish site
	@RequestMapping(value = "/sites", method = { RequestMethod.POST }, params = { "publish" })
	@SystemLog(operation="发布站点",operType=OPER_TYPE.publish)
	@Description("发布站点")
	@RequiresPermissions({ "app.modules.site.publish" })
	public void publish(@RequestBody @Valid Site site){
		String src = site.getDirDiskpath();
		String desc = site.getPubDiskpath();
		new File(desc).mkdirs();
		CmsFileUtils.sysncDirs(src, desc);
	}
}
