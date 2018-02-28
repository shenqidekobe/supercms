package com.dw.suppercms.domain.modules;

import java.util.List;
import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * CustomRepository
 *
 * @author osmos
 * @date 2015年7月14日
 */
public interface CustomRepository
		extends GenericDAO<Custom, Long> {

	/**
	 * get count of custom with the specified title
	 * 
	 * @param title custom's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of custom with the specified location
	 * 
	 * @param location custom location
	 * @return the count
	 */
	int countByLocation(String location);

	/**
	 * retrieve all customs with pagination and order by createTime desc
	 * 
	 * @param condition search condition map, the key limits in ['sortIds'];
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @param sorts get order by
	 * @return the paginate result
	 */
	SearchResult<Custom> paginateAll(Map<String, Object> condition, int startIndex, int maxResults, List<Sort> sorts);

}
