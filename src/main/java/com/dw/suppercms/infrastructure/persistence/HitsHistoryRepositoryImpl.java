package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.HitsHistory;
import com.dw.suppercms.domain.plugin.HitsHistoryRepository;

@Repository
public class HitsHistoryRepositoryImpl extends GenericRepositoryImpl<HitsHistory,Long> implements HitsHistoryRepository{

}
