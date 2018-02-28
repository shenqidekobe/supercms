package com.dw.suppercms.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.security.Role;
import com.dw.suppercms.domain.security.RoleRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * RoleRepositoryImpl
 *
 * @author osmos
 * @date 2015年9月17日
 */
@Repository
@SuppressWarnings("unchecked")
public class RoleRepositoryImpl
		extends GenericRepositoryImpl<Role, Long>
		implements RoleRepository {

	@Override
	public int countByName(String name) {
		String hql = "select count(*) from Role where name = :name";
		Object result = getSession().createQuery(hql)
				.setParameter("name", name)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Role> paginateAll(Map<String, Object> condition) {
		Search search = new Search(Role.class);
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}

	@Override
	public List<String> getPermissions(Long roleId) {
		String sql = "select permission from role_permission where role_id =:roleId";
		return getSession().createSQLQuery(sql)
				.setParameter("roleId", roleId)
				.list();
	}

	@Override
	public void clearPermission(Long roleId) {
		String sql = "delete from role_permission where role_id = :roleId";
		getSession().createSQLQuery(sql)
				.setParameter("roleId", roleId)
				.executeUpdate();
	}

	@Override
	public void assignPermission(Long roleId, List<String> permissions) {
		permissions.stream().forEach(new Consumer<String>() {
			@Override
			public void accept(String permission) {
				String sql = "insert into role_permission values(:roleId, :permission)";
				getSession().createSQLQuery(sql)
						.setParameter("roleId", roleId)
						.setParameter("permission", permission)
						.executeUpdate();
			}
		});
	}

}
