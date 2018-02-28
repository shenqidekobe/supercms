package com.dw.suppercms.application.manu.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.manu.SendUserIService;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.manu.SendUserServiceRepository;
import com.googlecode.genericdao.search.Search;

@ApplicationService
public class SendUserIServiceImpl implements SendUserIService{
	
	@Resource
	private SendUserServiceRepository sendUserServiceRepository;
	
	@Override
	public void createServiceUser(Long memberId,List<SendUserService> suLists) {
		for(SendUserService su:suLists){
			su.setUserId(memberId);
			if(su.getServiceId()!=null){
				this.sendUserServiceRepository.save(su);
			}
		}
	}

	@Override
	public void removeServiceUser(Long userId) {
		List<SendUserService> list= this.findServiceUserList(userId);
		for(SendUserService sendUserService:list){
			this.sendUserServiceRepository.remove(sendUserService);
		}
	}
	
	@Override
	public SendUserService findServiceUserByUserIdAndServiceId(Long userId,Long serviceId){
		return this.sendUserServiceRepository.findServiceUserByUserIdAndServiceId(userId,serviceId);
	}

	@Override
	public List<SendUserService> findServiceUserList(Long userId) {
		assertNotNull("userId must not be null", userId);
		List<SendUserService> list= this.sendUserServiceRepository.search(new Search().addFilterEqual("userId", userId));
		return list;
	}

}
