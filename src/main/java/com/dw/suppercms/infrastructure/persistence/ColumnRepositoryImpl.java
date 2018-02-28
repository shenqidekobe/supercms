package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.dw.suppercms.domain.security.User;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ColumnRepositoryImpl
 *
 * @author osmos
 * @date 2015年7月2日
 */
@Repository
public class ColumnRepositoryImpl
		extends GenericRepositoryImpl<Column, Long>
		implements ColumnRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Column where title = :title";
	public static final String COUNT_BY_DIR_NAME = "select count(*) from Column where dirName = :dirName";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByDirName(String dirName) {
		Object result = getSession().createQuery(COUNT_BY_DIR_NAME)
				.setParameter("dirName", dirName)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Column> retrieveBySite(Long siteId, int startIndex, int maxResults) {
		Search search = new Search(Column.class);
		search.addFilterEqual("siteId", siteId);
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		return searchAndCount(search);
	}

	@Override
	public List<Column> filterByDatasource(Long datasourceId) {
		Search search = new Search(Column.class);
		search.addFilterEqual("datasourceId", datasourceId);
		return search(search);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void queryColumnTreeByParentIds(List<Long> parentIds, Set<Long> columnIds) {
		if (parentIds.size() != 0) {
			columnIds.addAll(parentIds);
			List<Long> children = getSession().createQuery(
					"select t.id from Column t where t.parentId in(" + StringUtils.join(parentIds, ",") + ") ").list();
			queryColumnTreeByParentIds(children, columnIds);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Column> ownColumn() {
		String hql = "select t3 from UserDataSource t, Datasource t1, Column t3"
				+ " where t.datasourceId = t1.id "
				+ " and t1.id = t3.datasourceId"
				+ " and t.userId = :userId";
		return getSession().createQuery(hql)
				.setParameter("userId", ((User) SecurityUtils.getSubject().getPrincipal()).getId()).list();
	}
}
