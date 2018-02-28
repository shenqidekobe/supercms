package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.domain.system.ProduceLogRepository;

@Repository
public class ProduceLogRepositoryImpl extends GenericRepositoryImpl<ProduceLogInfo, Long> implements ProduceLogRepository {

}
