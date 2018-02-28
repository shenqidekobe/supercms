package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskRepository;

@Repository
public class JobTaskRepositoryImpl extends GenericRepositoryImpl<JobTaskInfo, Long> implements JobTaskRepository{

}
