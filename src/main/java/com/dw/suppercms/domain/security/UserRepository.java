package com.dw.suppercms.domain.security;

import java.util.Map;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * UserRepository
 *
 * @author osmos
 * @date 2015年9月17日
 */
public interface UserRepository
		extends GenericDAO<User, Long> {

	/**
	 * get count of user with the specified username
	 * 
	 * @param username user's username
	 * @return the count
	 */
	int countByUsername(String username);

	/**
	 * get count of user with the specified role
	 * 
	 * @param roleId role's id
	 * @return the count
	 */
	int countByRoleId(Long roleId);

	/**
	 * retrieve some users with condition
	 * 
	 * @param conditon the map key limits in [startIndex, maxResults]
	 * 
	 * @return paginated users
	 */
	SearchResult<User> paginateAll(Map<String, Object> condition);

	/**
	 * get user with specified username
	 * 
	 * @param username username
	 * @return matched user or null
	 */
	User getByUsername(String username);
}
