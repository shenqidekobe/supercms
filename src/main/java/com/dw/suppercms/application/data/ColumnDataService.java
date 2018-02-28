package com.dw.suppercms.application.data;

import java.util.List;

import com.dw.suppercms.domain.modules.Column;

public interface ColumnDataService {
	
	/**
	 * 更新栏目数据的索引状态
	 * */
	public void modifyColumnData(List<String> ids);
	

	/**
	 * 根据一组栏目ID和数据ID和模型ID确定一个栏目对象
	 * */
	public Column queryColumnByDataId(List<Long> columnIds, Long dataId,Long dataSourceId);

}
