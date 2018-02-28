package com.dw.suppercms.infrastructure.web.system;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.ProduceLogService;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 生成日志查询处理器
 * */
@RestController
@RequestMapping("systems")
public class ProduceLogsController extends BaseController{
	
	@Resource
	private ProduceLogService produceLogService;
	
	// retrieve logs represent as data table
	@RequestMapping(value = "/producelogs", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(String produceType,Long userId,String produceResult,String keys,Date startTime,Date endTime, int draw,int start, int length) {
		SearchResult<ProduceLogInfo> data = this.produceLogService.findProduceLogList(userId, produceType, produceResult, keys, startTime, endTime, start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}
	
	/**
	 * 获取详细信息
	 * */
	@RequestMapping(value = "/producelogs/{id}", method = { RequestMethod.GET })
	public ProduceLogInfo find(@PathVariable Long id) {
		return produceLogService.findProduceLog(id);
	}

}
