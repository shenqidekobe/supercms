package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.security.UserDataSource;
import com.dw.suppercms.domain.security.UserDataSourceRepository;

@Repository
public class UserDataSourceRepositoryImpl extends GenericRepositoryImpl<UserDataSource, Long> implements UserDataSourceRepository{

}
