package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.IdeasBoxInfo;
import com.dw.suppercms.domain.plugin.IdeasBoxRepository;

@Repository
public class IdeasBoxRepositoryImpl extends GenericRepositoryImpl<IdeasBoxInfo,Long> implements IdeasBoxRepository {

}
