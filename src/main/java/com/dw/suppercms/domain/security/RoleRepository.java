package com.dw.suppercms.domain.security;

import java.util.List;
import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * RoleRepository
 *
 * @author osmos
 * @date 2015年9月17日
 */
public interface RoleRepository
		extends GenericDAO<Role, Long> {

	/**
	 * get count of role with the specified name
	 * 
	 * @param rolename role's name
	 * @return the count
	 */
	int countByName(String name);

	/**
	 * retrieve some roles with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults]
	 * 
	 * @return paginated roles
	 */
	SearchResult<Role> paginateAll(Map<String, Object> condition);
	
	List<String> getPermissions(Long roleId);
	void clearPermission(Long roleId);
	void assignPermission(Long roleId, List<String> permissions);
	
}
