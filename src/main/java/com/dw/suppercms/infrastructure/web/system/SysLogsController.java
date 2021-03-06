package com.dw.suppercms.infrastructure.web.system;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.SysLogService;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.domain.system.SysLogInfo.MODULE_LOG;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 日志查询处理器
 * */
@RestController
@RequestMapping("systems")
public class SysLogsController extends BaseController{
	
	@Autowired
	private SysLogService sysLogService;
	
	
	// retrieve logs represent as data table
	@RequestMapping(value = "/systemlogs", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(String moduleLog,String operType,Long userId,String operId,String keys,Date startTime,Date endTime, int draw,int start, int length) {
		MODULE_LOG module=StringUtils.isEmpty(moduleLog)?null:MODULE_LOG.valueOf(MODULE_LOG.class,moduleLog);
		SearchResult<SysLogInfo> data = sysLogService.findLogsList(module, operType, userId, operId, keys, startTime, endTime, start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}
	
	
	/**
	 * 获取详细信息
	 * */
	@RequestMapping(value = "/systemlogs/{id}", method = { RequestMethod.GET })
	public SysLogInfo find(@PathVariable Long id) {
		return sysLogService.findById(id);
	}


}
