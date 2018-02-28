package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * ModelRepositoryImpl
 *
 * @author osmos
 * @date 2015年7月31日
 */
@Repository
@SuppressWarnings("unchecked")
public class ModelRepositoryImpl
		extends GenericRepositoryImpl<Model, Long>
		implements ModelRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Model where title = :title";
	public static final String COUNT_BY_TABLE_CODE = "select count(*) from Model where tableCode = :tableCode";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}
	
	@Override
	public int countByTableCode(String tableCode) {
		Object result = getSession().createQuery(COUNT_BY_TABLE_CODE)
				.setParameter("tableCode", tableCode)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Model> paginateAll(Map<String, Object> condition) {
		Search search = new Search(Model.class);
		search.setSorts((List<Sort>) condition.get("sorts"));
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}

}
