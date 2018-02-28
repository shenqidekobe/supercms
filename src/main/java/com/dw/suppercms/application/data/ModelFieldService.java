package com.dw.suppercms.application.data;

import java.util.Map;

import com.dw.suppercms.domain.data.ModelField;
import com.googlecode.genericdao.search.SearchResult;

/**
 * ModelFieldService
 *
 * @author osmos
 * @date 2015年8月3日
 */
public interface ModelFieldService {

	/**
	 * validate title when creating or updating model field
	 * 
	 * @param title model's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * validate field code when creating or updating model field
	 * 
	 * @param fieldCode model's field code
	 * @return true if valid, false if invalid
	 */
	public Boolean validateFieldCode(String fieldCode);

	/**
	 * create a new model field
	 * 
	 * @param modelField holds the new model field state
	 * @return the saved model field
	 */
	ModelField create(ModelField modelField);

	/**
	 * retrieve a model field
	 * 
	 * @param id model field's id
	 * @return the retrieved model field
	 */
	ModelField retrieve(Long id);

	/**
	 * update a model field
	 * 
	 * @param id the id of the updating model
	 * @param modelField holds the new model field state
	 * @return the updated model field
	 */
	ModelField update(Long id, ModelField modelField);

	/**
	 * delete a model field
	 * 
	 * @param id the id of the deleting model field
	 * @return the deleted model field's id
	 */
	Long delete(Long id);

	/**
	 * retrieve model fields with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults, sorts]
	 * 
	 * @return paginated model fields
	 */
	SearchResult<ModelField> paginateAll(Map<String, Object> condition);
}
