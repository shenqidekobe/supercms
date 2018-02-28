package com.dw.suppercms.domain.modules;

import java.util.List;
import java.util.Set;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ColumnRepository
 *
 * @author osmos
 * @date 2015年7月2日
 */
public interface ColumnRepository
		extends GenericDAO<Column, Long> {

	/**
	 * get count of column with the specified title
	 * 
	 * @param title column's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of column with the specified directory name
	 * 
	 * @param dirName column directory name
	 * @return the count
	 */
	int countByDirName(String dirName);

	/**
	 * retrieve columns by site
	 *
	 * @param siteId column' siteId
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return retrieved columns
	 */
	SearchResult<Column> retrieveBySite(Long siteId, int startIndex, int maxResults);

	void queryColumnTreeByParentIds(List<Long> parentIds, Set<Long> columnIds);

	/**
	 * filter all columns with the specified datasource id
	 * 
	 * @param datasourceId
	 * @return filtered columns
	 */
	List<Column> filterByDatasource(Long datasourceId);

	List<Column> ownColumn();
}
