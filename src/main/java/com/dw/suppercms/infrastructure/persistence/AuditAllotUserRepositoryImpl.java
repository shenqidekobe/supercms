package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.AuditAllotUser;
import com.dw.suppercms.domain.manu.AuditAllotUserRepository;

@Repository
public class AuditAllotUserRepositoryImpl extends GenericRepositoryImpl<AuditAllotUser,Long> implements AuditAllotUserRepository {

}
