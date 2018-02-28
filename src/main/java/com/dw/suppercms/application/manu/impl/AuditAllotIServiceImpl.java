package com.dw.suppercms.application.manu.impl;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.manu.AuditAllotIService;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.manu.AuditAllotService;
import com.dw.suppercms.domain.manu.AuditAllotServiceRepository;
import com.dw.suppercms.domain.manu.AuditAllotUser;
import com.dw.suppercms.domain.manu.AuditAllotUserRepository;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;
import com.googlecode.genericdao.search.Search;

/**
 * 审核分配
 * */
@ApplicationService
public class AuditAllotIServiceImpl implements AuditAllotIService{
	
	@Resource
	private AuditAllotServiceRepository auditAllotServiceRepository;
	
	@Resource
	private AuditAllotUserRepository auditAllotUserRepository;
	
	@Resource
	private ManuscriptMemberRepository manuscriptMemberRepository;
	
	@Resource
	private DatasourceRepository datasourceRepository;
	
	@Override
	public void createAuditAllotService(Long auditUserId, Object[] serviceIds) {
		assertNotNull("auditUserId must not be null", auditUserId);
		if(serviceIds==null||serviceIds.length<1){
			return;
		}
		AuditAllotService obj=null;
		for(Object serviceId:serviceIds){
			obj=new AuditAllotService();
			obj.setAuditId(auditUserId);
			obj.setServiceId(Long.parseLong(serviceId.toString()));
			obj.setCreateUserId(null);
			this.auditAllotServiceRepository.save(obj);
		}
	}

	@Override
	public void createAuditAllotUser(Long auditUserId, Object[] userIds) {
		assertNotNull("auditUserId must not be null", auditUserId);
		if(userIds==null||userIds.length<1){
			return;
		}
		AuditAllotUser obj=null;
		for(Object userId:userIds){
			obj=new AuditAllotUser();
			obj.setAuditId(auditUserId);
			obj.setUserId(Long.parseLong(userId.toString()));
			obj.setCreateUserId(null);
			this.auditAllotUserRepository.save(obj);
		}
	}
	
	@Override
	public void removeAuditAllot(Long auditUserId) {
		assertNotNull("auditUserId must not be null", auditUserId);
		List<AuditAllotService> list=this.auditAllotServiceRepository.search(new Search().addFilterEqual("auditId", auditUserId));
		
		if(!CollectionUtils.isEmpty(list)){
			this.auditAllotServiceRepository.remove((AuditAllotService[])list.toArray());
			for(AuditAllotService auditAllotService:list){
				this.auditAllotServiceRepository.remove(auditAllotService);
			}
		}
		
		List<AuditAllotUser> list2=this.auditAllotUserRepository.search(new Search().addFilterEqual("auditId", auditUserId));
			
		if(!CollectionUtils.isEmpty(list2)){
			for(AuditAllotUser auditAllotUser:list2){
				this.auditAllotUserRepository.remove(auditAllotUser);
			}
		}
	}

	@Override
	public List<ManuscriptMember> findAllotUserListByAudit(Long auditId) {
		assertNotNull("auditId must not be null", auditId);
		List<AuditAllotUser> ulist=this.auditAllotUserRepository.search(new Search().addFilterEqual("auditId", auditId));
		List<ManuscriptMember> resultList=new ArrayList<ManuscriptMember>();
		if(!CollectionUtils.isEmpty(ulist)){
			for(AuditAllotUser aau:ulist){
				resultList.add(aau.getMember());
			}
		}
		return resultList;
	}

	@Override
	public List<Datasource> findAllotServiceListByAudit(Long auditId) {
		assertNotNull("auditId must not be null", auditId);
		
		List<AuditAllotService> slist=this.auditAllotServiceRepository.search(new Search().addFilterEqual("auditId", auditId));
		List<Datasource> resultList=new ArrayList<Datasource>();
		if(!CollectionUtils.isEmpty(slist)){
			for(AuditAllotService aas:slist){
				resultList.add(aas.getDatasource());
			}
		}
		return resultList;
	}

}
