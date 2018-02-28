package com.dw.suppercms.infrastructure.persistence;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.manu.Manuscript;
import com.dw.suppercms.domain.manu.ManuscriptRepository;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;
import com.dw.suppercms.support.Pager;

@Repository
public class ManuscriptRepositoryImpl extends GenericRepositoryImpl<Manuscript,Long> implements ManuscriptRepository {

	@Override
	public Long findManuscriptCreateCount(final Long createUserId, final String searchKey,final Long serviceId,final ManuStatus status) {
		String hql = "SELECT COUNT(*) FROM Manuscript t WHERE 1=1";
		if(createUserId!=null){
			hql += " and t.createCmsUserId=" + createUserId+"";
		}
		if(status!=null){
			hql += " and t.manuStatus='" + status+"'";
		}
		if(StringUtils.isNotEmpty(searchKey)){
			hql += " and (t.title like '%" + searchKey+"%' or t.content like '%"+searchKey+"%')";
		}
		if(serviceId!=null){
			hql += " and exists(from ManuscriptService ms where ms.manuId=t.manuId and ms.serviceId="+serviceId+")";
		}
		Query query = getSession().createQuery(hql);
		Long num = (Long) query.uniqueResult();
		return num;
	}
	
	@Override
	public Long findManuscriptAuditCount(final Long auditUserId, final String searchKey,final Long serviceId,final ManuStatus status) {
		String hql = "SELECT COUNT(*) FROM Manuscript t WHERE 1=1";
		if(auditUserId!=null){
			hql += " and t.auditUserId=" + auditUserId+"";
		}
		if(status!=null){
			hql += " and t.manuStatus='" + status+"'";
		}
		if(StringUtils.isNotEmpty(searchKey)){
			hql += " and (t.title like '%" + searchKey+"%' or t.content like '%"+searchKey+"%')";
		}
		if(serviceId!=null){
			hql += " and exists(from ManuscriptService ms where ms.manuId=t.manuId and ms.serviceId="+serviceId+")";
		}
		Query query = getSession().createQuery(hql);
		Long num = (Long) query.uniqueResult();
		return num;
	}

	@Override
	public List<Manuscript> findManuscriptCreateList(final Long createUserId,
			final String searchKey,final Long serviceId,final ManuStatus status, final Pager pager) {
		String hql = "FROM Manuscript t WHERE 1=1";
		if(createUserId!=null){
			hql += " and t.createCmsUserId=" + createUserId+"";
		}
		if(status!=null){
			hql += " and t.manuStatus='" + status+"'";
		}
		if(StringUtils.isNotEmpty(searchKey)){
			hql += " and (t.title like '%" + searchKey+"%' or t.content like '%"+searchKey+"%')";
		}
		if(serviceId!=null){
			hql += " and exists(from ManuscriptService ms where ms.manuId=t.manuId and ms.serviceId="+serviceId+")";
		}
		String sort = pager.getSort();
		if (!StringUtils.isEmpty(sort)) {
			hql += " order by " + sort;
			String dir = pager.getDir();
			if (!StringUtils.isEmpty(dir)) {
				hql += " " + dir;
			}
		}
		Query query = getSession().createQuery(hql);
		query.setMaxResults(pager.getPageSize());
		query.setFirstResult(pager.getStartIndex());
		@SuppressWarnings("unchecked")
		List<Manuscript> list = query.list();
		return list;
	}

	@Override
	public Manuscript findLastManuscriptByCreateUser(final Long createUserId) {
		String hql = "from Manuscript where manuStatus='" + ManuStatus.draft + "' and createCmsUserId=" +createUserId + "order by createTime desc";
		@SuppressWarnings("unchecked")
		List<Manuscript> list = getSession().createQuery(hql).list();
		if(list.size() ==0 ){
			return null;
		}
		return list.get(0);
	}
	
	@Override
	public List<Manuscript> findManuscriptAuditList(final Long auditUserId,final String searchKey,final Long serviceId,final ManuStatus status,final Pager pager){
		String hql = "FROM Manuscript t WHERE 1=1";
		if(auditUserId!=null){
			hql += " and t.auditUserId=" + auditUserId+"";
		}
		if(status!=null){
			hql += " and t.manuStatus='" + status+"'";
		}
		if(StringUtils.isNotEmpty(searchKey)){
			hql += " and t.title like '%" + searchKey+"%'";
		}
		if(serviceId!=null){
			hql += " and exists(from ManuscriptService ms where ms.manuId=t.manuId and ms.serviceId="+serviceId+")";
		}
		String sort = pager.getSort();
		if (!StringUtils.isEmpty(sort)) {
			hql += " order by " + sort;
			String dir = pager.getDir();
			if (!StringUtils.isEmpty(dir)) {
				hql += " " + dir;
			}
		}
		Query query = getSession().createQuery(hql);
		query.setMaxResults(pager.getPageSize());
		query.setFirstResult(pager.getStartIndex());
		@SuppressWarnings("unchecked")
		List<Manuscript> list = query.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Manuscript> findManuscriptAuditListByService(final Long auditUserId,
			final Integer count, final String lockTime) {
		String sql="select * from t_manuscript t where exists(" +
				"select * from manuscript_service ms where ms.MANU_ID=t.MANU_ID and exists (" +
				"select * from audit_allot_service aas where  aas.SERVICE_ID=ms.SERVICE_ID and aas.audit_id="+auditUserId+")) " +
				"and t.manu_status='"+ManuStatus.waitAudit+"' and (t.LOCK_TIME<'"+lockTime+"' or t.LOCK_TIME is null or t.LOCK_USER_ID="+auditUserId+") order by t.create_time desc limit "+count;
		Query query=getSession().createSQLQuery(sql).addEntity(Manuscript.class);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Manuscript> findManuscriptAuditListByUser(final Long auditUserId,
			final Integer count, final String lockTime,final List<Long> inManuIds) {
		String sql="select * from t_manuscript t where exists(" +
				"select * from audit_allot_user aau where  aau.user_id=t.create_cms_user_id and aau.audit_id="+auditUserId+")" +
				"and t.manu_status='"+ManuStatus.waitAudit+"' and (t.LOCK_TIME<'"+lockTime+"' or t.LOCK_TIME is null or t.LOCK_USER_ID="+auditUserId+")";
		if(!CollectionUtils.isEmpty(inManuIds)){
			String commaDelimitedIds = CommonsUtil.collectionToInArgs(inManuIds);
			sql+=" and t.manu_id not in("+commaDelimitedIds+")";
		}
		sql+=" order by t.create_time desc limit "+count;
		Query query=getSession().createSQLQuery(sql).addEntity(Manuscript.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Manuscript> findManuscriptAuditListUntreated(final Long auditUserId,final String lockTime){
		String sql="select * from t_manuscript t where  manu_status='"+ManuStatus.waitAudit+"' and t.LOCK_USER_ID="+auditUserId+" and t.LOCK_TIME>='"+lockTime+"'";
		Query query=getSession().createSQLQuery(sql).addEntity(Manuscript.class);
		return query.list();
	}
	
	@Override
	public Long findWaitAuditManuscriptCount(final Long auditUserId,final String lockTime){
		String sql="select count(*) from t_manuscript t where (exists(" +
				"select * from manuscript_service ms where ms.MANU_ID=t.MANU_ID and exists (" +
				"select * from audit_allot_service aas where  aas.SERVICE_ID=ms.SERVICE_ID and aas.audit_id="+auditUserId+"))" +
				"or exists(select * from audit_allot_user aau where  aau.user_id=t.create_cms_user_id and aau.audit_id="+auditUserId+")) and " +
				"t.manu_status='"+ManuStatus.waitAudit+"'";
		if(StringUtils.isNotEmpty(lockTime)){
			sql+=" and (t.LOCK_TIME is null or t.LOCK_TIME<='"+lockTime+"')";
		}
		Query query=getSession().createSQLQuery(sql);
		BigInteger num = (BigInteger) query.uniqueResult();
		return num.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Manuscript> findManuscriptAuditAllotList(final Long auditUserId,
			final String searchKey,final Long serviceId, final ManuStatus status,final Pager pager) {
		String sql="select * from t_manuscript t where (exists(" +
				"select * from manuscript_service ms where ms.manu_id=t.manu_id ";
		if(serviceId!=null){
			sql+=" and ms.service_id="+serviceId;
		}
		sql+=" and exists (" +
				"select * from audit_allot_service aas where  aas.service_id=ms.service_id and aas.audit_id="+auditUserId+"))" +
				"or exists(select * from audit_allot_user aau where  aau.user_id=t.create_cms_user_id and aau.audit_id="+auditUserId+")) and " +
				"t.manu_status='"+status+"'";
		if(StringUtils.isNotEmpty(searchKey)){
			sql += " and (t.title like '%" + searchKey+"%' or t.content like '%"+searchKey+"%')";
		}
		String sort = pager.getSort();
		if (!StringUtils.isEmpty(sort)) {
			sql += " order by t.create_time " ;
			String dir = pager.getDir();
			if (!StringUtils.isEmpty(dir)) {
				sql += " " + dir;
			}
		}
		Query query=getSession().createSQLQuery(sql).addEntity(Manuscript.class);
		query.setMaxResults(pager.getPageSize());
		query.setFirstResult(pager.getStartIndex());
		return query.list();
	}

	@Override
	public Long findManuscriptAuditAllotCount(final Long auditUserId,
			final String searchKey, final Long serviceId, final ManuStatus status) {
		String sql="select count(*) from t_manuscript t where (exists(" +
				"select * from manuscript_service ms where ms.manu_id=t.manu_id ";
		if(serviceId!=null){
			sql+="and ms.service_id="+serviceId;
		}
		sql+=" and exists (" +
				"select * from audit_allot_service aas where  aas.service_id=ms.service_id and aas.audit_id="+auditUserId+"))" +
				"or exists(select * from audit_allot_user aau where  aau.user_id=t.create_cms_user_id and aau.audit_id="+auditUserId+")) and " +
				"t.manu_status='"+status+"'";
		if(StringUtils.isNotEmpty(searchKey)){
			sql += " and (t.title like '%" + searchKey+"%' or t.content like '%"+searchKey+"%')";
		}
		Query query=getSession().createSQLQuery(sql);
		BigInteger num = (BigInteger) query.uniqueResult();
		return num.longValue();
	}

}
