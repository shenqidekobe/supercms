package com.dw.suppercms.application.manu;

import java.util.List;

import com.dw.suppercms.domain.manu.SendUserService;

public interface SendUserIService {
	
	void createServiceUser(Long memberId,List<SendUserService> suLists);
	
	void removeServiceUser(Long userId);
	
	SendUserService findServiceUserByUserIdAndServiceId(Long userId,Long serviceId);
	
	List<SendUserService> findServiceUserList(Long userId);

}
