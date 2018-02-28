package com.dw.suppercms.domain.manu;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface SendUserServiceRepository extends GenericDAO<SendUserService,Long>{
	
	SendUserService findServiceUserByUserIdAndServiceId(Long userId,Long serviceId);

}
