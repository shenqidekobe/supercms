package com.dw.suppercms.domain.data;

import java.util.List;
import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * DatasourceRepository
 *
 * @author osmos
 * @date 2015年7月28日
 */
public interface DatasourceRepository
		extends GenericDAO<Datasource, Long> {

	/**
	 * get count of datasource with the specified title
	 * 
	 * @param title datasource's title
	 * @return the count
	 */
	int countByTitle(String title);
	
	
	/**
	 * retrieve some datasources with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults, sorts]
	 * 
	 * @return paginated datasources
	 */
	SearchResult<Datasource> paginateAll(Map<String, Object> condition);
	
	List<Datasource> getDatasources(Long userId);
	
	void clearDatasources(Long userId);
}
