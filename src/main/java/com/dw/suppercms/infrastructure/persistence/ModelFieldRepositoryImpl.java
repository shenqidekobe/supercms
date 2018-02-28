package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelFieldRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * ModelFieldRepositoryImpl
 *
 * @author osmos
 * @date 2015年8月3日
 */
@Repository
@SuppressWarnings("unchecked")
public class ModelFieldRepositoryImpl
		extends GenericRepositoryImpl<ModelField, Long>
		implements ModelFieldRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from ModelField where title = :title";
	public static final String COUNT_BY_FIELD_CODE = "select count(*) from ModelField where fieldCode = :fieldCode";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByFieldCode(String fieldCode) {
		Object result = getSession().createQuery(COUNT_BY_FIELD_CODE)
				.setParameter("fieldCode", fieldCode)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<ModelField> paginateAll(Map<String, Object> condition) {
		Search search = new Search(ModelField.class);
		search.addFilterEqual("modelId", (Long) condition.get("modelId"));
		search.setSorts((List<Sort>) condition.get("sorts"));
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}

	@Override
	public void removeFieldsOfModel(Long modelId) {
		String hql = "delete from ModelField where modelId = " + modelId;
		getSession().createQuery(hql).executeUpdate();
	}
}
