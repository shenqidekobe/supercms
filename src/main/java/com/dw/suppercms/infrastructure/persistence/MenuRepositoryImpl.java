package com.dw.suppercms.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.security.Menu;
import com.dw.suppercms.domain.security.MenuRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * MenuRepositoryImpl
 *
 * @author osmos
 * @date 2015年9月24日
 */
@Repository
@SuppressWarnings("unchecked")
public class MenuRepositoryImpl
		extends GenericRepositoryImpl<Menu, Long>
		implements MenuRepository {

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery("select count(*) from Menu where title = :title")
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Menu> paginateAll(Map<String, Object> condition) {
		Search search = new Search(Menu.class);
		search.setFirstResult((int) condition.get("firstResult"));
		search.setMaxResults((int) condition.get("maxResults"));
		return searchAndCount(search);
	}

	@Override
	public List<Menu> getMenusByPerms(List<String> perms) {
		List<Menu> list = new ArrayList<Menu>();
		String hql = "from Menu where uisref in(:perms)";
		if (!CollectionUtils.isEmpty(perms)) {
			list = getSession().createQuery(hql).setParameterList("perms", perms).list();
		}
		return list;
	}
}
