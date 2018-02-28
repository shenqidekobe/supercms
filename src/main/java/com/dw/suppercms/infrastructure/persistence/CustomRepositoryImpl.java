package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.CustomRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * CustomRepositoryImpl
 *
 * @author osmos
 * @date 2015年7月14日
 */
@Repository
public class CustomRepositoryImpl
		extends GenericRepositoryImpl<Custom, Long>
		implements CustomRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Custom where title = :title";
	public static final String COUNT_BY_LOCATION = "select count(*) from Custom where location = :location";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByLocation(String dirName) {
		Object result = getSession().createQuery(COUNT_BY_LOCATION)
				.setParameter("location", dirName)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<Custom> paginateAll(Map<String, Object> condition, int startIndex, int maxResults,
			List<Sort> sorts) {
		Search search = new Search(Custom.class);
		if (condition.containsKey("sortIds")) {
			List<Long> sortIds = (List<Long>) condition.get("sortIds");
			if (sortIds.size() == 1) {
				search.addFilterEqual("sortId", sortIds.get(0));
			} else {
				search.addFilterIn("sortId", sortIds);
			}
		}
		if (sorts != null) {
			search.setSorts((List<Sort>) condition.get("sorts"));
		}
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		SearchResult<Custom> searchAndCount = searchAndCount(search);

		return searchAndCount;
	}

}
