package com.dw.suppercms.application.data;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.data.Tag;
import com.googlecode.genericdao.search.SearchResult;

/**
 * TagService
 *
 * @author osmos
 * @date 2015年9月8日
 */
public interface TagService {

	/**
	 * create tag
	 * 
	 * @param tag
	 */
	Tag create(Tag tag);

	/**
	 * retrieve a tag
	 * 
	 * @param id tag's id
	 */
	Tag retrieve(Long id);

	/**
	 * update a tag
	 * 
	 * @param tag holds the updating tag state
	 */
	Tag update(Long id, Tag tag);

	/**
	 * delete a tag
	 * 
	 * @param id tag's id
	 */
	void delete(Long id);

	/**
	 * deal tag on data
	 * 
	 * @param datasourceId
	 * @param dataId
	 * @param tagIds
	 */
	void dealTags(Long datasourceId, Long dataId, List<Long> tagIds);

	/**
	 * validate title when creating or updating tag
	 * 
	 * @param title tag's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * retrieve some tags with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults, sorts]
	 * 
	 * @return paginated tags
	 */
	SearchResult<Tag> paginateAll(Map<String, Object> condition);

	/**
	 * @return all tags
	 */
	List<Tag> all();

	/**
	 * retrieve tags on data
	 * 
	 * @param datasourceId data's datasourceId
	 * @param dataId data's id
	 * @return ownTags
	 */
	List<Tag> tagsOnData(Long datasourceId, Long dataId);
	
	
}
