package com.dw.suppercms.domain.data;

import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * TagRepository
 *
 * @date 2015年9月8日
 */
public interface TagRepository
		extends GenericDAO<Tag, Long> {

	/**
	 * get count of tag with the specified title
	 * 
	 * @param title tag's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of tag that as ref to the data of the model
	 * 
	 * @param tag
	 * @param model data's model
	 * @return the count
	 */
	int countByRef(Tag tag, Model model);

	/**
	 * retrieve some tags with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults, sorts]
	 * 
	 * @return paginated tags
	 */
	SearchResult<Tag> paginateAll(Map<String, Object> condition);

}
