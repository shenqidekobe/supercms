package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.Tag;
import com.dw.suppercms.domain.data.TagRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * TagRepositoryImpl
 *
 * @author osmos
 * @date 2015年9月8日
 */
@Repository
@SuppressWarnings("unchecked")
public class TagRepositoryImpl
		extends GenericRepositoryImpl<Tag, Long>
		implements TagRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Tag where title = :title";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByRef(Tag tag, Model model) {
		String hql = "select count(*) from TagData where modelId = :modelId and tagId = :tagId";
		Object result = getSession().createQuery(hql)
				.setParameter("modelId", model.getId())
				.setParameter("tagId", tag.getId())
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Tag> paginateAll(Map<String, Object> condition) {
		Search search = new Search(Tag.class);
		search.setSorts((List<Sort>) condition.get("sorts"));
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}
}
