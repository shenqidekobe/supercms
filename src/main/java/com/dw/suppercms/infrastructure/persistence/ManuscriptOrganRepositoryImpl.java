package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.dw.suppercms.domain.manu.ManuscriptOrganRepository;

@Repository
public class ManuscriptOrganRepositoryImpl extends GenericRepositoryImpl<ManuscriptOrgan, Long> implements ManuscriptOrganRepository{

}
