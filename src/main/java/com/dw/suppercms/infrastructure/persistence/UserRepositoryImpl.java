package com.dw.suppercms.infrastructure.persistence;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.security.UserRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * UserRepositoryImpl
 *
 * @author osmos
 * @date 2015年9月17日
 */
@Repository
public class UserRepositoryImpl
		extends GenericRepositoryImpl<User, Long>
		implements UserRepository {

	@Override
	public int countByUsername(String username) {
		String hql = "select count(*) from User where username = :username";
		Object result = getSession().createQuery(hql)
				.setParameter("username", username)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByRoleId(Long roleId) {
		String hql = "select count(*) from User where roleId = :roleId";
		Object result = getSession().createQuery(hql)
				.setParameter("roleId", roleId)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<User> paginateAll(Map<String, Object> condition) {
		Search search = new Search(User.class);
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}
	
	@Override
	public User getByUsername(String username) {
		Search search = new Search(User.class);
		search.addFilterEqual("username", username);
		return searchUnique(search);
	}
}
