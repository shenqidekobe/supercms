package com.dw.suppercms.application.system;

import java.util.Date;

import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.domain.system.SysLogInfo.MODULE_LOG;
import com.googlecode.genericdao.search.SearchResult;

public interface SysLogService {
	
	/**
	 * 创建日志
	 * @param log
	 * */
	boolean createSysLog(SysLogInfo log);
	
	/**
	 * 检索日志列表
	 * */
	SearchResult<SysLogInfo> findLogsList(MODULE_LOG moduleLogs,String operType,Long userId,String operId,String keys,Date startTime,Date endTime,int startIndex,int maxResults );
	
	/**
	 * 获取详细信息
	 * */
	SysLogInfo findById(Long id);
}
