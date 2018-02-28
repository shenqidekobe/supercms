package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.PlugInInfo;
import com.dw.suppercms.domain.plugin.PlugInRepository;
import com.dw.suppercms.infrastructure.utils.Pager;

@Repository
public class PlugInRepositoryImpl extends GenericRepositoryImpl<PlugInInfo,Long> implements PlugInRepository{
	
	@SuppressWarnings("unchecked")
	@Override
	public PlugInInfo queryByName(String name) {
		String hql = "from PlugInInfo where fieldName='" +name + "'";
		List<PlugInInfo> list = getSession().createQuery(hql).list();
		if(list.size() ==0 ){
			return null;
		}
		return list.get(0);
	}

	@Override
	public Long queryPlugInCount() {
		String hql = "SELECT count(*) FROM PlugInInfo t";
		Query query = getSession().createQuery(hql);
		return (Long) query.uniqueResult();
	}

	@Override
	public List<PlugInInfo> queryPlugInList(final Pager pager) {
		String hql = "FROM PlugInInfo t ";
		String sort = pager.getSort();
		if (!StringUtils.isEmpty(sort)) {
			hql += " order by " + sort;
			String dir = pager.getDir();
			if (!StringUtils.isEmpty(dir)) {
				hql += " " + dir;
			}
		}
		Query query =  getSession().createQuery(hql);
		query.setMaxResults(pager.getPageSize());
		query.setFirstResult(pager.getStartIndex());
		@SuppressWarnings("unchecked")
		List<PlugInInfo> list = query.list();
		return list;
	}

}
