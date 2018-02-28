package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.AuditAllotService;
import com.dw.suppercms.domain.manu.AuditAllotServiceRepository;

@Repository
public class AuditAllotServiceRepositoryImpl extends GenericRepositoryImpl<AuditAllotService,Long> implements AuditAllotServiceRepository{

}
