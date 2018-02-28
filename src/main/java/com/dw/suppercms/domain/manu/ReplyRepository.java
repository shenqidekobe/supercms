package com.dw.suppercms.domain.manu;

import java.util.List;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface ReplyRepository extends GenericDAO<ReplyInfo,Long>{
	
	List<ReplyInfo> findReplyByManuId(Long manuId);
	
	public ReplyInfo findReplyFirstByCreateUserId(Long manuId,String createUserId);

}
