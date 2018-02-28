package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.domain.system.SysLogRepository;

@Repository
public class SysLogRepositoryImpl extends GenericRepositoryImpl<SysLogInfo, Long> implements SysLogRepository {


}
