package com.dw.suppercms.application.index;

import java.util.List;

/**
 * 数据索引接口
 * */
public interface IndexSearchService {
	
	/**
	 * 重建需要索引的数据
	 * */
	void pushAll();
	
	/**
	 * 推送数据到索引服务器
	 * */
	void pushData(Long datasourceId,Long id);
	
	/**
	 * 删除索引服务器的数据
	 * @param popIds
	 * */
	void popData(List<String> popIds);

}
