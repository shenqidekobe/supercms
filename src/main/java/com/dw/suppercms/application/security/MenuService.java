package com.dw.suppercms.application.security;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.security.Menu;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * MenuService
 *
 * @author osmos
 * @date 2015年9月24日
 */
public interface MenuService {

	/**
	 * validate title when creating or updating menu
	 * 
	 * @param title menu's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * create a new menu
	 * 
	 * @param menu holds the new menu state
	 * @return the saved menu
	 */
	Menu create(Menu menu);

	/**
	 * retrieve a menu
	 * 
	 * @param id menu id
	 * @return the retrieved menu
	 */
	Menu retrieve(Long id);

	/**
	 * update a menu
	 * 
	 * @param id the id of the updating menu
	 * @param newMenu holds the new menu state
	 * @return the updated menu
	 */
	Menu update(Long id, Menu newMenu);

	/**
	 * delete a menu
	 * 
	 * @param id the id of the deleting menu
	 * @return the deleted menu's id
	 */
	Long delete(Long id);

	/**
	 * retrieve menus with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults]
	 * 
	 * @return paginated menus
	 */
	SearchResult<Menu> paginateAll(Map<String, Object> condition);
	
	/**
	 * retrieve all menus
	 * 
	 * @return all menus
	 */
	List<Menu> all();

	List<Menu> retrieveMenusByPerms(List<String> perms);
}
