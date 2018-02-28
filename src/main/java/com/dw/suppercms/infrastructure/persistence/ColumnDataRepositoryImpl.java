package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;

@Repository
@SuppressWarnings("unchecked")
public class ColumnDataRepositoryImpl
		extends GenericRepositoryImpl<ColumnData, Long>
		implements ColumnDataRepostitory {

	@Override
	public void deleteByDataIdAndDatasourceId(Long dataId, Long datasourceId) {
		String hql = "delete from ColumnData where dataId=" + dataId + " and datasourceId=" + datasourceId;
		getSession().createQuery(hql).executeUpdate();
	}

	@Override
	public List<ColumnData> retrieveColumnsByDataIds(Long[] dataIds) {
		String hql = "from ColumnData t where t.dataId in (:ids)";
		return getSession().createQuery(hql).setParameterList("ids", dataIds).list();
	}
	
	@Override
	public ColumnData findColumnDataByDataIdAndDatasourceId(Long dataId, Long datasourceId){
		String hql = "from ColumnData where dataId=" + dataId + " and datasourceId=" + datasourceId;
		Query query=getSession().createQuery(hql);
		List<ColumnData> list=query.list();
		return list.size()>0?list.get(0):null;
	}
	
	@Override
	public List<Long> findDataIds(String tableCode, Long datasourceId) {
		String sql = "select id from " + tableCode + " where datasource_id =:datasourceId";
		return getSession().createSQLQuery(sql).addScalar("id", StandardBasicTypes.LONG).setParameter("datasourceId", datasourceId).list();
	}
	
	@Override
	public void addColumnData(Long columnId, Long modelId, Long datasourceId, List<Long> dataIds) {
		for (Long dataId : dataIds) {
			ColumnData columnData = new ColumnData();
			columnData.setColumnId(columnId);
			columnData.setDataId(dataId);
			columnData.setDatasourceId(datasourceId);
			columnData.setIndexState(false);
			columnData.setModelId(modelId);
			columnData.setProduceState(false);
			save(columnData);
			
		}
	}
	
	@Override
	public void deleteColumnData(Long columnId, Long datasourceId) {
		String hql = "delete from ColumnData where columnId =:columnId and datasourceId =:datasourceId";
		getSession().createQuery(hql).setParameter("columnId", columnId).setParameter("datasourceId", datasourceId).executeUpdate();
	}
}
