package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.domain.templates.TemplateRepository;

@Repository
public class TemplateRepositoryImpl extends GenericRepositoryImpl<TemplateInfo, Long> implements TemplateRepository{

}
