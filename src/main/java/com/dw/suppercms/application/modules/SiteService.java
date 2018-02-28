package com.dw.suppercms.application.modules;

import java.util.List;

import com.dw.suppercms.domain.modules.Site;
import com.googlecode.genericdao.search.SearchResult;

/**
 * SiteService
 *
 * @author osmos
 * @date 2015年6月12日
 */
public interface SiteService {

	/**
	 * validate title when creating or updating site
	 * 
	 * @param title site's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * validate testDomain when creating or updating site
	 * 
	 * @param testDomain site's testDomain
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTestDomain(String testDomain);

	/**
	 * validate productDomain when creating or updating site
	 * 
	 * @param productDomain site's productDomain
	 * @return true if valid, false if invalid
	 */
	public Boolean validateProductDomain(String productDomain);

	/**
	 * validate directory name when creating or updating site
	 * 
	 * @param dirName site's dirName
	 * @return true if valid, false if invalid
	 */
	public Boolean validateDirName(String dirName);

	/**
	 * create a new site
	 * 
	 * @param site holds the new site state
	 * @return the saved site
	 */
	Site create(Site site);

	/**
	 * retrieve a site
	 * 
	 * @param id site's id
	 * @return the retrieved site
	 */
	Site retrieve(Long id);

	/**
	 * update a site
	 * 
	 * @param id the id of the updating site
	 * @param newSite holds the new site state
	 * @return the updated site
	 */
	Site update(Long id, Site newSite);

	/**
	 * delete a site
	 * 
	 * @param id the id of the deleting site
	 * @return the deleted site's id
	 */
	Long delete(Long id);

	/**
	 * retrieve all sites
	 * 
	 * @return all sites
	 */
	List<Site> all();

	/**
	 * retrieve all sites with pagination
	 * 
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return pagination sites
	 */
	SearchResult<Site> paginateAll(int startIndex, int maxResults);

}
