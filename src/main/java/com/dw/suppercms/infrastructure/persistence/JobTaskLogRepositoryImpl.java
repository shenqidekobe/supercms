package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.JobTaskLogInfo;
import com.dw.suppercms.domain.system.JobTaskLogRepository;

@Repository
public class JobTaskLogRepositoryImpl extends GenericRepositoryImpl<JobTaskLogInfo, Long> implements JobTaskLogRepository{

}
