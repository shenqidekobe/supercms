package com.dw.suppercms.domain.security;

import java.util.List;
import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * MenuRepository
 *
 * @author osmos
 * @date 2015年9月24日
 */
public interface MenuRepository
		extends GenericDAO<Menu, Long> {

	/**
	 * get count of menu with the specified title
	 * 
	 * @param title menu's title
	 * @return the count
	 */
	int countByTitle(String title);

	/**
	 * retrieve menus with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults]
	 * 
	 * @return paginated menus
	 */
	SearchResult<Menu> paginateAll(Map<String, Object> condition);
	
	List<Menu> getMenusByPerms(List<String> perms);

}
