package com.dw.suppercms.application.modules;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.modules.Custom;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * CustomService
 *
 * @author osmos
 * @date 2015年7月14日
 */
public interface CustomService {

	/**
	 * validate title when creating or updating custom
	 * 
	 * @param title custom's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * validate location when creating or updating custom
	 * 
	 * @param location custom's location
	 * @return true if valid, false if invalid
	 */
	public Boolean validateLocation(String location);

	/**
	 * create a new custom
	 * 
	 * @param custom holds the new custom state
	 * @return the saved custom
	 */
	Custom create(Custom custom);

	/**
	 * retrieve a custom
	 * 
	 * @param id custom's id
	 * @return the retrieved custom
	 */
	Custom retrieve(Long id);

	/**
	 * update a custom
	 * 
	 * @param id the id of the updating custom
	 * @param custom holds the new custom state
	 * @return the updated custom
	 */
	Custom update(Long id, Custom custom);

	/**
	 * delete a custom
	 * 
	 * @param id the id of the deleting custom
	 * @return the deleted custom's id
	 */
	Long delete(Long id);

	/**
	 * retrieve all customs with pagination and order by createTime desc
	 * 
	 * @param condition search condition map, the key limits in ['sortId'];
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @param sorts get order by
	 * @return paginated customs
	 */
	SearchResult<Custom> paginateAll(Map<String, Object> condition, int startIndex, int maxResults, List<Sort> sorts);
	
	/**
	 * findCustomListBySortId
	 * @param sortId
	 * @return List<Custom>
	 * */
	List<Custom> findCustomListBySortId(Long sortId);

}
