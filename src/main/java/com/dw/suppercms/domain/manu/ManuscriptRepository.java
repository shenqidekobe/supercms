package com.dw.suppercms.domain.manu;

import java.util.List;

import com.dw.suppercms.support.ManuscriptDto.ManuStatus;
import com.dw.suppercms.support.Pager;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface ManuscriptRepository extends GenericDAO<Manuscript,Long>{
	
	public Manuscript findLastManuscriptByCreateUser(Long createUserId);
	
	public Long findManuscriptCreateCount(Long createUserId,String searchKey,Long serviceId, ManuStatus status);
	
	public Long findManuscriptAuditCount(Long auditUserId,String searchKey,Long serviceId, ManuStatus status);
	
	public List<Manuscript> findManuscriptCreateList(Long createUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	
	public List<Manuscript> findManuscriptAuditList(Long auditUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	
	//按分配的数据源获取待审核列表
	public List<Manuscript> findManuscriptAuditListByService(Long auditUserId,Integer count,String lockTime);
	//按分配的单位获取待审核列表
	public List<Manuscript> findManuscriptAuditListByUser(Long auditUserId,Integer count,String lockTime,List<Long> inManuIds);
	//获取我拿过未处理完的待审核列表数据
	public List<Manuscript> findManuscriptAuditListUntreated(Long auditUserId,String lockTime);
	//获取待审核的数量
	public Long findWaitAuditManuscriptCount(Long auditUserId,String lockTime);
	//获取审核员已分配的栏目和投稿人的所有的稿件信息
	public List<Manuscript> findManuscriptAuditAllotList(Long auditUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	public Long findManuscriptAuditAllotCount(Long auditUserId,String searchKey,Long serviceId,ManuStatus status);

}
