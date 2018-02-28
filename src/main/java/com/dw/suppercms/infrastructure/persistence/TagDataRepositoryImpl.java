package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.Tag;
import com.dw.suppercms.domain.data.TagData;
import com.dw.suppercms.domain.data.TagDataRepository;

/**
 * 
 * TagDataRepositoryImpl
 *
 * @author osmos
 * @date 2015年9月9日
 */
@Repository
@SuppressWarnings("unchecked")
public class TagDataRepositoryImpl
		extends GenericRepositoryImpl<TagData, Long>
		implements TagDataRepository {

	@Override
	public void deleteTagsOnData(Long datasourceId, Long dataId) {
		String hql = "delete from TagData t "
				+ "where t.datasourceId = :datasourceId "
				+ "and t.dataId = :dataId";
		getSession().createQuery(hql)
				.setParameter("datasourceId", datasourceId)
				.setParameter("dataId", dataId)
				.executeUpdate();
	}

	@Override
	public List<Tag> tagsOnData(Long datasourceId, Long dataId) {
		String hql = "select t1 from Tag t1, TagData t2 "
				+ "where t1.id=t2.tagId "
				+ "and t2.datasourceId = :datasourceId "
				+ "and t2.dataId = :dataId";
		return getSession().createQuery(hql)
				.setParameter("datasourceId", datasourceId)
				.setParameter("dataId", dataId)
				.list();
	}
}
