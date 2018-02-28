package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;

@Repository
public class ManuscriptMemberRepositoryImpl extends GenericRepositoryImpl<ManuscriptMember, Long> implements ManuscriptMemberRepository {

}
