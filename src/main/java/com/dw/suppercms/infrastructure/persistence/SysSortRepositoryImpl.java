package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.googlecode.genericdao.search.Search;

@Repository
public class SysSortRepositoryImpl extends GenericRepositoryImpl<SysSortInfo, Long> implements SysSortRepository {

	@SuppressWarnings("unchecked")
	public List<SysSortInfo> findRootSortList(String type) {
		String hql = "from SysSortInfo where parent is null and sortType=?";
		Query query = getSession().createQuery(hql);
		query.setString(0, type);
		return query.list();
	}

	@Override
	public List<SysSortInfo> findSorts(SORT_TYPE type) {
		Search search = new Search(SysSortInfo.class);
		search.addFilterEqual("sortType", type);
		return search(search);
	}

}
