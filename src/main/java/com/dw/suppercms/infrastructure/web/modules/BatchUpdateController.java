package com.dw.suppercms.infrastructure.web.modules;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.modules.ColumnService;
import com.dw.suppercms.application.modules.CustomService;
import com.dw.suppercms.application.modules.SiteService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.infrastructure.web.BaseController;

/**
 * 批量更新控制器
 * */
@RestController
@RequestMapping("/batchs")
public class BatchUpdateController extends BaseController{
	
	@Autowired
	private CustomService customService;
	
	@Autowired
	private ColumnService columnService;
	

	@Autowired
	private SiteService siteService;
	
	/**
	 * 获取所有的站点
	 * */
	@RequestMapping(value = "/sites", method = { RequestMethod.GET })
	public List<Site> findSiteList() {
		return siteService.all();
	}

	
	/**
	 * 根据站点ID获取栏目列表
	 * */
	@RequestMapping(value="/columns",method={RequestMethod.GET})
	public String findColumnList(Long siteId){
		if(siteId==null){
			return JSONSerializer().deepSerialize(null);
		}
		List<Column> list= this.columnService.findRootColumn(siteId);
		return JSONSerializer().deepSerialize(list);
	}
	
	/**
	 * 根据分类获取自定义页面列表
	 * */
	@RequestMapping(value="/customs", method = { RequestMethod.GET })
	public List<Custom> findCustomList(Long sortId){
		return this.customService.findCustomListBySortId(sortId);
	}

}
