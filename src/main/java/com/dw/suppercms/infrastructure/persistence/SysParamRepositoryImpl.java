package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.system.SysParamInfo;
import com.dw.suppercms.domain.system.SysParamRepository;

@Repository
public class SysParamRepositoryImpl extends GenericRepositoryImpl<SysParamInfo, Long> implements SysParamRepository{

}
