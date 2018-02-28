package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.domain.templates.TemplateSnippetRepository;

@Repository
public class TemplateSnippetRepositoryImpl extends GenericRepositoryImpl<TemplateSnippet, Long> implements TemplateSnippetRepository{

}
