package com.dw.suppercms.application.data;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.data.Model;
import com.googlecode.genericdao.search.SearchResult;

/**
 * ModelService
 *
 * @author osmos
 * @date 2015年7月31日
 */
public interface ModelService {

	/**
	 * validate title when creating or updating model
	 * 
	 * @param title model's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * validate table code when creating or updating model
	 * 
	 * @param title model's table code
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTableCode(String tableCode);

	/**
	 * create a new model
	 * 
	 * @param model holds the new model state
	 * @return the saved model
	 */
	Model create(Model model);

	/**
	 * retrieve a model
	 * 
	 * @param id model's id
	 * @return the retrieved model
	 */
	Model retrieve(Long id);

	/**
	 * update a model
	 * 
	 * @param id the id of the updating model
	 * @param newmodel holds the new model state
	 * @return the updated model
	 */
	Model update(Long id, Model newmodel);

	/**
	 * delete a model
	 * 
	 * @param id the id of the deleting model
	 * @return the deleted model's id
	 */
	Long delete(Long id);

	/**
	 * retrieve all models
	 * 
	 * @return all models
	 */
	List<Model> all();

	/**
	 * retrieve some models with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults, sorts]
	 * 
	 * @return paginated models
	 */
	SearchResult<Model> paginateAll(Map<String, Object> condition);

}
