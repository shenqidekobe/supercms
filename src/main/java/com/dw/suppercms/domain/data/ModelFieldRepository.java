package com.dw.suppercms.domain.data;

import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * ModelFieldRepository
 *
 * @author osmos
 * @date 2015年8月3日
 */
public interface ModelFieldRepository
		extends GenericDAO<ModelField, Long> {

	/**
	 * get count of model field with the specified title
	 * 
	 * @param title model field's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of model field with the specified field code
	 * 
	 * @param fieldCode model field's code
	 * @return the count
	 */
	int countByFieldCode(String fieldCode);

	/**
	 * retrieve all model fields with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults, sorts]
	 * 
	 * @return paginated model fields
	 */
	SearchResult<ModelField> paginateAll(Map<String, Object> condition);

	/**
	 * delete all fields of the spcecified model
	 * 
	 * @param modelId model's id
	 */
	void removeFieldsOfModel(Long modelId);
}
