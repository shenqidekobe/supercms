package com.dw.suppercms.application.manu;

import java.util.List;

import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.manu.ManuscriptMember;

/**
 * 稿件审核分配服务接口
 * */
public interface AuditAllotIService {
	
	/**
	 * 分配数据源给审核员
	 * */
	void createAuditAllotService(Long auditUserId,Object[] serviceIds);
	
	/**
	 * 分配单位用户给审核员
	 * */
	void createAuditAllotUser(Long auditUserId,Object[] userIds);
	
	/**
	 * 删除审核员的分配信息
	 * */
	void removeAuditAllot(Long auditUserId);
	
	/**
	 * 获取已分配的用户列表
	 * */
	List<ManuscriptMember> findAllotUserListByAudit(Long auditId);
	
	/**
	 * 获取已分配的数据源列表
	 * */
	List<Datasource> findAllotServiceListByAudit(Long auditId);

}
