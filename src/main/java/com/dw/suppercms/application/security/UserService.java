package com.dw.suppercms.application.security;

import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.security.User;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * UserService
 *
 * @author osmos
 * @date 2015年9月17日
 */
public interface UserService {

	/**
	 * validate username when creating or updating user
	 * 
	 * @param username user' username
	 * @return true if valid, false if invalid
	 */
	public Boolean validateUsername(String username);

	/**
	 * create a new user
	 * 
	 * @param user holds the new user state
	 * @return the saved user
	 */
	User create(User user);

	/**
	 * retrieve a user
	 * 
	 * @param id user's id
	 * @return the retrieved user
	 */
	User retrieve(Long id);

	/**
	 * update a user
	 * 
	 * @param id the id of the updating user
	 * @param newuser holds the new user state
	 * @return the updated user
	 */
	User update(Long id, User newuser);

	/**
	 * delete a user
	 * 
	 * @param id the id of the deleting user
	 * @return the deleted user's id
	 */
	Long delete(Long id);

	/**
	 * retrieve some users with condition
	 * 
	 * @param conditon the map key limits in [firstResult, maxResults]
	 * 
	 * @return paginated users
	 */
	SearchResult<User> paginateAll(Map<String, Object> condition);
	
	User retriveByUsername(String username);
	
	/**
	 * all users
	 * */
	List<User> all();
}
