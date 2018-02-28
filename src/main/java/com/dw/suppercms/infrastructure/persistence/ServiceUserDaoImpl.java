package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.manu.SendUserServiceRepository;

@Repository
public class ServiceUserDaoImpl extends GenericRepositoryImpl<SendUserService,Long> implements SendUserServiceRepository{
	
	@SuppressWarnings("unchecked")
	@Override
	public SendUserService findServiceUserByUserIdAndServiceId(final Long userId,final Long serviceId){
		String hql = "FROM SendUserService WHERE userId=? and serviceId=?";
		Query query = getSession().createQuery(hql);
		query.setLong(0, userId);
		query.setLong(1,serviceId);
		List<SendUserService> list= query.list();
		return list==null||list.size()<1?new SendUserService():list.get(0);
	}

}
