package com.dw.suppercms.domain.manu;

import java.util.List;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface ManuscriptServiceRepository extends GenericDAO<ManuscriptService,Long> {
	
	List<ManuscriptService> queryManuscriptServiceByManuId(Long manuId);

}
