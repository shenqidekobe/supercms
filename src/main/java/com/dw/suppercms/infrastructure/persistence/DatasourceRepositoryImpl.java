package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * DatasourceRepositoryImpl
 *
 * @author osmos
 * @date 2015年7月28日
 */
@Repository
@SuppressWarnings("unchecked")
public class DatasourceRepositoryImpl
		extends GenericRepositoryImpl<Datasource, Long>
		implements DatasourceRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Datasource where title = :title";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Datasource> paginateAll(Map<String, Object> condition) {
		Search search = new Search(Datasource.class);
		if (condition.containsKey("sortIds")) {
			List<Long> sortIds = (List<Long>) condition.get("sortIds");
			if (sortIds.size() == 1) {
				search.addFilterEqual("sortId", sortIds.get(0));
			} else {
				search.addFilterIn("sortId", sortIds);
			}
		}
		search.setSorts((List<Sort>) condition.get("sorts"));
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}

	@Override
	public List<Datasource> getDatasources(Long userId) {
		String hql = "select t2 from UserDataSource t, Datasource t2 where t.datasourceId = t2.id and t.userId = :userId";
		return getSession().createQuery(hql).setParameter("userId", userId).list();
	}
	
	@Override
	public void clearDatasources(Long userId) {
		String hql = "delete from UserDataSource t where t.userId = :userId";
		getSession().createQuery(hql).setParameter("userId", userId).executeUpdate();
	}
}
