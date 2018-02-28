package com.dw.suppercms.domain.data;

import java.util.List;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface ColumnDataRepostitory extends GenericDAO<ColumnData, Long> {

	void deleteByDataIdAndDatasourceId(Long dataId, Long datasourceId);

	List<ColumnData> retrieveColumnsByDataIds(Long[] dataIds);
	
	ColumnData findColumnDataByDataIdAndDatasourceId(Long dataId, Long datasourceId);
	
	List<Long> findDataIds(String tableCode, Long datasourceId);
	void addColumnData(Long columnId,Long modelId, Long datasourceId, List<Long> dataIds);
	void deleteColumnData(Long columnId, Long datasourceId);
}
