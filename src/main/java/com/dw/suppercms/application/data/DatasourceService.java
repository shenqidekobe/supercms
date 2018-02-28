package com.dw.suppercms.application.data;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.data.Datasource;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * DatasourceService
 *
 * @author osmos
 * @date 2015年7月28日
 */
public interface DatasourceService {

	/**
	 * validate title when creating or updating datasource
	 * 
	 * @param title datasource's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * create a new datasource
	 * 
	 * @param datasource holds the new datasource state
	 * @return the saved datasource
	 */
	Datasource create(Datasource datasource);

	/**
	 * retrieve a datasource
	 * 
	 * @param id datasource's id
	 * @return the retrieved datasource
	 */
	Datasource retrieve(Long id);

	/**
	 * update a datasource
	 * 
	 * @param id the id of the updating datasource
	 * @param newdatasource holds the new datasource state
	 * @return the updated datasource
	 */
	Datasource update(Long id, Datasource newdatasource);

	/**
	 * delete a datasource
	 * 
	 * @param id the id of the deleting datasource
	 * @return the deleted datasource's id
	 */
	Long delete(Long id);

	/**
	 * retrieve all datasources
	 * 
	 * @return all datasources
	 */
	List<Datasource> all();

	/**
	 * retrieve some datasources with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults, sorts]
	 * 
	 * @return paginated datasources
	 */
	SearchResult<Datasource> paginateAll(Map<String, Object> condition);

	List<Long> retrieveDatasourceIds(Long userId);
	List<Datasource> retrieveDatasources(Long userId);

	void assignDatasources(Long userId, List<Long> datasourceIds);

}
