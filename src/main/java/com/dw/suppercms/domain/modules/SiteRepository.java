package com.dw.suppercms.domain.modules;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * SiteRepository
 *
 * @author osmos
 * @date 2015年6月12日
 */
public interface SiteRepository
		extends GenericDAO<Site, Long> {

	/**
	 * get count of site with the specified title
	 * 
	 * @param title site's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * get count of site with the specified testDomain
	 * 
	 * @param testDomain site's testDomain
	 * @return the count
	 */
	int countByTestDomain(String testDomain);

	/**
	 * get count of site with the specified productDomain
	 * 
	 * @param productDomain site's productDomain
	 * @return the count
	 */
	int countByProductDomain(String productDomain);
	
	/**
	 * get count of site with the specified directory name
	 * 
	 * @param dirName site directory name
	 * @return the count
	 */
	int countByDirName(String dirName);

	/**
	 * retrieve all sites with pagination and order by createTime desc
	 * 
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return the paginate result
	 */
	SearchResult<Site> paginateAll(int startIndex, int maxResults);

}
