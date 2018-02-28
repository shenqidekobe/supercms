package com.dw.suppercms.application.security;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.security.Role;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * RoleService
 *
 * @author osmos
 * @date 2015年9月17日
 */
public interface RoleService {

	/**
	 * validate rolename when creating or updating role
	 * 
	 * @param name role' name
	 * @return true if valid, false if invalid
	 */
	public Boolean validateName(String name);

	/**
	 * create a new role
	 * 
	 * @param role holds the new role state
	 * @return the saved role
	 */
	Role create(Role role);

	/**
	 * retrieve a role
	 * 
	 * @param id role's id
	 * @return the retrieved role
	 */
	Role retrieve(Long id);

	/**
	 * update a role
	 * 
	 * @param id the id of the updating role
	 * @param newrole holds the new role state
	 * @return the updated role
	 */
	Role update(Long id, Role newrole);

	/**
	 * delete a role
	 * 
	 * @param id the id of the deleting role
	 * @return the deleted role's id
	 */
	Long delete(Long id);
	
	/**
	 * retrieve all roles
	 * 
	 * @return all roles
	 */
	List<Role> all();

	/**
	 * retrieve some roles with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults]
	 * 
	 * @return paginated roles
	 */
	SearchResult<Role> paginateAll(Map<String, Object> condition);

	List<String> retrievePermissions(Long roleId);
	
	void assignPermissions(Long roleId, List<String> permissions);
	
}
