package com.dw.suppercms.domain.system;

import java.util.List;

import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface SysSortRepository extends GenericDAO<SysSortInfo, Long> {

	/**
	 * 查询顶级分类集合
	 * */
	public List<SysSortInfo> findRootSortList(String type);

	public List<SysSortInfo> findSorts(SORT_TYPE type);
}
