package com.dw.suppercms.application.system;

import java.util.Date;

import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 生成日志接口
 * */
public interface ProduceLogService {
	
	/**
	 * 创建生成日志
	 * @param produceLog
	 * @return boolean
	 * */
	boolean createProduceLog(ProduceLogInfo produceLog);
	
	/**
	 * 查看详情
	 * @param id
	 * @return produceLog
	 * */
	ProduceLogInfo findProduceLog(Long id);
	
	/**
	 * 检索生成日志列表
	 * */
	SearchResult<ProduceLogInfo> findProduceLogList(Long userId,String produceType,String produceResult,String keys,Date startTime,Date endTime,int startIndex,int maxResults);

}
