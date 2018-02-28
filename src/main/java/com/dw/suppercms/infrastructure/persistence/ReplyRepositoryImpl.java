package com.dw.suppercms.infrastructure.persistence;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.ReplyInfo;
import com.dw.suppercms.domain.manu.ReplyRepository;

@Repository
public class ReplyRepositoryImpl extends GenericRepositoryImpl<ReplyInfo,Long> implements ReplyRepository{

	@Override
	public List<ReplyInfo> findReplyByManuId(final Long manuId) {
		String hql = "FROM ReplyInfo t WHERE manuId=?";
		hql += " order by createTime desc";
		Query query = getSession().createQuery(hql);
		query.setLong(0, manuId);
		@SuppressWarnings("unchecked")
		List<ReplyInfo> list = query.list();
		return list;
	}
	
	public ReplyInfo findReplyFirstByCreateUserId(final Long manuId,
			final String createUserId){
		String hql = "FROM ReplyInfo t WHERE manuId=? and createUserId=?";
		hql += " order by createTime asc";
		Query query = getSession().createQuery(hql);
		query.setLong(0, manuId);
		query.setString(1, createUserId);
		@SuppressWarnings("unchecked")
		List<ReplyInfo> list = query.list();
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

}
