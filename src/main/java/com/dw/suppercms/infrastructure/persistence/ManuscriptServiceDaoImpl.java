package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.ManuscriptService;
import com.dw.suppercms.domain.manu.ManuscriptServiceRepository;

@Repository
public class ManuscriptServiceDaoImpl extends GenericRepositoryImpl<ManuscriptService,Long> implements ManuscriptServiceRepository {

	@Override
	public List<ManuscriptService> queryManuscriptServiceByManuId(final Long manuId) {
		String hql = "FROM ManuscriptService t WHERE manuId=?";
		Query query = getSession().createQuery(hql);
		query.setLong(0, manuId);
		@SuppressWarnings("unchecked")
		List<ManuscriptService> list = query.list();
		return list;
	}

}
