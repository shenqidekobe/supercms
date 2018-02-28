package com.dw.suppercms.application.data.impl;

import java.util.List;

import javax.annotation.Resource;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.data.ColumnDataService;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.googlecode.genericdao.search.Search;

/**
 * 栏目数据关系服务接口实现
 * */
@ApplicationService
public class ColumnDataServiceImpl implements ColumnDataService{
	
	@Resource
	private ColumnDataRepostitory columnDataRepostitory;
	
	@Resource
	private ColumnRepository columnRepository;
	
	@Override
	public void modifyColumnData(List<String> ids) {
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			String[] splits = id.split("_");
			Long columnId = Long.valueOf(splits[0]);
			Long dataId = Long.valueOf(splits[1]);
			ColumnData columnData = null;
			try {
				Search search=new Search();
				search.addFilterEqual("dataId", dataId);
				search.addFilterEqual("columnId", columnId);
				columnData=this.columnDataRepostitory.searchUnique(search);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (columnData != null) {
				columnData.setIndexState(true);
				columnDataRepostitory.save(columnData);
			} 
			if (i % 30 == 0) {
				columnDataRepostitory.flush();
			}
		}
	}

	@Override
	public Column queryColumnByDataId(List<Long> columnIds, Long dataId,
			Long dataSourceId) {
		Search search=new Search();
		search.addFilterEqual("dataId", dataId);
		search.addFilterEqual("datasourceId", dataSourceId);
		search.addFilterEqual("produceState", true);
		List<ColumnData> columnDatas=this.columnDataRepostitory.search(search);
		for (ColumnData cd:columnDatas) {
			Long columnId=cd.getColumnId();
			if(columnIds.contains(columnId)){
				return  columnRepository.find(columnId);
			}
		}
		return null;
	}

}
