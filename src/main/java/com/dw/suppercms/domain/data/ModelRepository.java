package com.dw.suppercms.domain.data;

import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ModelRepository
 *
 * @author osmos
 * @date 2015年7月31日
 */
public interface ModelRepository
		extends GenericDAO<Model, Long> {

	/**
	 * get count of model with the specified title
	 * 
	 * @param title model's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of model with the specified table code
	 * 
	 * @param title model's title
	 * @return the count
	 */
	int countByTableCode(String tableCode);

	/**
	 * retrieve some models with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults, sorts]
	 * 
	 * @return paginated models
	 */
	SearchResult<Model> paginateAll(Map<String, Object> condition);
}
