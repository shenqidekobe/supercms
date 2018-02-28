package com.dw.suppercms.application.manu;

import java.sql.Timestamp;
import java.util.List;

import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.manu.Manuscript;
import com.dw.suppercms.domain.manu.ManuscriptService;
import com.dw.suppercms.domain.manu.ReplyInfo;
import com.dw.suppercms.support.ManuscriptDto;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;
import com.dw.suppercms.support.Pager;

/**
 * 稿件服务接口
 * */
public interface ManuscriptIService {
	
	/**
	 * 创建草稿
	 * */
	Manuscript createManuscriptDraft(Manuscript obj,String content,Long[] serviceIds,boolean isInterval);

	/**
	 * 创建稿件
	 * */
	Manuscript createManuscript(Manuscript obj,String content,Long[] serviceIds);
	
	/**
	 * 修改稿件实体
	 * */
	Manuscript modifyManuscript(Manuscript obj,Long[] serviceIds);
	
	/**
	 * 删除稿件(逻辑删除)
	 * */
	void removeManuscript(Long id);
	
	/**
	 * 删除用户的草稿信息
	 * */
	void removeManuscriptDraft(Long userId);
	
	/**
	 * 更改稿件状态 
	 * */
	Manuscript modifyManuscript(Long id,Long userId,ManuStatus status);
	
	/**
	 * 撤回稿件
	 * */
	void modifyRecallManuscript(Long id,Long userId);
	
	/**
	 * 更新稿件的锁定时间
	 * */
	void modifyManuscriptLockTime(Long manuId,Long lockUserId,Timestamp lockTime);
	
	/**
	 * 更新修改的锁定时间
	 * */
	void modifyManuscriptModifyTime(Long manuId,Timestamp modifyTime);
	
	/**
	 * 查询稿件按ID
	 * */
	Manuscript findById(Long id);
	
	/**
	 * 获取创建人最后一条保存的草稿稿件
	 * */
	Manuscript findLastManuscriptByCreateUser(Long createUserId);
	
	/**
	 * 获取创建人最后一条保存的草稿稿件
	 * */
	ManuscriptDto findLastManuscriptDtoByCreateUser(Long createUserId);
	
	/**
	 * 获取投稿人的稿件数量
	 * */
	Long findManuscriptCreateCount(Long createUserId,String searchKey,Long serviceId,ManuStatus status);
	
	/**
	 * 获取审核人已审核的稿件数量
	 * */
	Long findManuscriptAuditCount(Long auditUserId,String searchKey,Long serviceId,ManuStatus status);
	
	/**
	 * 分页检索稿件列表,投稿人获取稿件列表
	 * */
	List<ManuscriptDto> findManuscriptCreateList(Long createUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	
	/**
	 * 分页检索审核的稿件列表,审核人员获取稿件列表
	 * */
	List<ManuscriptDto> findManuscriptAuditList(Long auditUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	
	/**
	 * 分页检索审核员可审核的稿件列表
	 * */
	List<ManuscriptDto> findManuscriptAuditAllotList(Long auditUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager);
	
	/**
	 * 检索审核员可审核的稿件数量s
	 * */
	Long findManuscriptAuditAllotCount(Long auditUserId,String searchKey,Long serviceId,ManuStatus status);
	
	
	/**
	 * 获取审核稿件，指定每次获取count条，且对获取的条数进行锁定,优先按数据源获取，其次是按单位用户
	 * */
	List<ManuscriptDto> findAuditManuscript(Long auditUserId,Integer count);
	
	/**
	 * 获取当前审核员所有待审核的稿件数量
	 * @param isLock:是否查询非锁定的稿件
	 * */
	Long findWaitAuditManuscriptCount(Long auditUserId,boolean isNoLock);
	
	/**
	 * 获取审核员未处理完的待审核稿件列表
	 * */
	List<ManuscriptDto> findManuscriptAuditListUntreated(Long auditUserId);
	
	/**
	 * 获取稿件Dto对象
	 * */
	ManuscriptDto findManuscriptDtoById(Long id);
	
	/**
	 * 获取投稿人的数据源列表
	 * */
	List<Datasource> findServiceByCreateUserId(Long createUserId);
	
	/**
	 * 获取审核人可审核的栏目列表
	 * */
	List<Datasource> findServiceByAuditUserId(Long auditUserId);
	
	/**
	 * 获取稿件的栏目
	 * */
	List<ManuscriptService> findManuscriptServiceByManuId(Long manuId);
	
	void saveDataService(String writer,String title,String shortTitle,String from,String editor,String content,Long serviceId,Long createUserId,Long manuId);
	
	
	
	
	/**
	 * 创建稿件批复记录
	 * */
	ReplyInfo createReplyEntity(Long manuId,ManuStatus status,String replyDesc);
	
	/**
	 * 修改稿件的时候 修改批复记录
	 * */
	ReplyInfo modifyReplyEntity(Long id,Long manuId,ManuStatus status,String replyDesc);
	
	/**
	 * 修改稿件的时候，删除批复记录
	 * */
	void removeReply(Long id);
	
	
	/**
	 * 稿件的批复记录
	 * */
	List<ReplyInfo> findReplyEntityByManuId(Long manuId);
	
	/**
	 * 获取投稿人的稿件备注信息
	 * */
	ReplyInfo findReplyFirstByCreateUserId(Long manuId,Long createUserId);
	

}
