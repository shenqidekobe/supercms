package com.dw.suppercms.domain.data;

import java.util.List;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

/**
 * 
 * TagDataRepository
 *
 * @author osmos
 * @date 2015年9月9日
 */
public interface TagDataRepository
		extends GenericDAO<TagData, Long> {

	void deleteTagsOnData(Long datasourceId, Long dataId);

	List<Tag> tagsOnData(Long datasourceId, Long dataId);
}
