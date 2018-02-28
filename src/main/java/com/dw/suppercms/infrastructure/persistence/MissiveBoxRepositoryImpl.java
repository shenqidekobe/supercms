package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.MissiveBoxInfo;
import com.dw.suppercms.domain.plugin.MissiveBoxRepository;

@Repository
public class MissiveBoxRepositoryImpl extends GenericRepositoryImpl<MissiveBoxInfo,Long> implements MissiveBoxRepository {

}
