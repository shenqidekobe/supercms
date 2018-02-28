package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.PraiseHistory;
import com.dw.suppercms.domain.plugin.PraiseHistoryRepository;

@Repository
public class PraiseHistoryRepositoryImpl extends GenericRepositoryImpl<PraiseHistory,Long> implements PraiseHistoryRepository{

}
