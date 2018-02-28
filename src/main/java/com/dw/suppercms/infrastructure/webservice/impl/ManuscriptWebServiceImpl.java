package com.dw.suppercms.infrastructure.webservice.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.dw.suppercms.application.manu.ManuscriptIService;
import com.dw.suppercms.application.manu.ManuscriptMemberService;
import com.dw.suppercms.application.manu.SendUserIService;
import com.dw.suppercms.application.plugin.PraiseService;
import com.dw.suppercms.application.system.SysLogService;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.manu.Manuscript;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptService;
import com.dw.suppercms.domain.manu.ReplyInfo;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.infrastructure.common.ConstantConfig;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.utils.MD5;
import com.dw.suppercms.infrastructure.webservice.support.DataObjectTransformUtils;
import com.dw.suppercms.support.DefinedExplain;
import com.dw.suppercms.support.ManuscriptDto;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;
import com.dw.suppercms.support.ManuscriptServiceDto;
import com.dw.suppercms.support.MemberDto;
import com.dw.suppercms.support.MemberServiceDto;
import com.dw.suppercms.support.Pager;
import com.dw.suppercms.support.ReplyDto;
import com.dw.suppercms.support.ServiceDto;
import com.dw.suppercms.webservice.ManuscriptWebService;

/**
 * 投稿系统接口实现
 * */
@WebService(endpointInterface = "com.dw.suppercms.webservice.ManuscriptWebService")
public class ManuscriptWebServiceImpl implements ManuscriptWebService{
	
	@Resource
	private ManuscriptIService manuscriptIService;
	
	@Resource
	private ManuscriptMemberService manuscriptMemberService;
	
	@Resource
	private SendUserIService sendUserIService;
	
	@Resource
	private SysLogService sysLogService;
	
	@Resource
	private PraiseService praiseService;
	
	@Resource
	private ConstantConfig constantConfig;

	@Override
	public MemberDto login(String loginName, String password, String memberType,String authCode) {
		Assert.notNull(loginName);
		Assert.notNull(password);
		MemberDto dto=new MemberDto();
		Integer result=DefinedExplain.RESULT_FLAG.NAME_NOT_EXIST;
		Boolean isExistName=this.manuscriptMemberService.validateLoginName(loginName);
		if(!isExistName){
			ManuscriptMember manuscriptMember=this.manuscriptMemberService.findMemberByLoginNameAndPassword(loginName, password);
			if(manuscriptMember==null){
				result=DefinedExplain.RESULT_FLAG.PASSWORD_WRONG;
			}else{
				result=manuscriptMember.getMemberType().toString().equals(memberType)?DefinedExplain.RESULT_FLAG.SUCCESS:DefinedExplain.RESULT_FLAG.TYPE_NOT_MATCH;
				if(result==DefinedExplain.RESULT_FLAG.SUCCESS){
					dto=DataObjectTransformUtils.memberToMemberDto(manuscriptMember);
				}
			}
		}
		
		dto.setLoginFlag(result);
		return dto;
	}

	@Override
	public MemberDto findMemberById(Long id) {
		Assert.notNull(id,"id must not be null.");
		ManuscriptMember manuscriptMember=this.manuscriptMemberService.findMemberById(id);
		Assert.notNull(manuscriptMember);
		MemberDto dto=DataObjectTransformUtils.memberToMemberDto(manuscriptMember);
		return dto;
	}

	@Override
	public Integer updateMemberPassword(Long id, String oldPass, String newPass) {
		Assert.notNull(id,"id must not be null.");
		Assert.notNull(oldPass,"oldPass must not be null");
		Assert.notNull(id,"newPass must not be null.");
		ManuscriptMember manuscriptMember=this.manuscriptMemberService.findMemberById(id);
		oldPass=MD5.getMD5(oldPass);
		if(!manuscriptMember.getPassword().equals(oldPass)){
			return DefinedExplain.RESULT_FLAG.OLD_PASS_NOT_MATCH;
		}
		manuscriptMember.setPassword(newPass);
		this.manuscriptMemberService.update(id, manuscriptMember);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Integer updateMemberBaseInfo(Long id, String memberName,
			String phone, String email, String authCode) {
		Assert.notNull(id,"id must not be null.");
		ManuscriptMember manuscriptMember=this.manuscriptMemberService.findMemberById(id);
		manuscriptMember.setMemberName(memberName);
		manuscriptMember.setPhone(phone);
		manuscriptMember.setEmail(email);
		this.manuscriptMemberService.update(id, manuscriptMember);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Integer saveOperLog(Long userId, String operType, String content,
			String ip) {
		Assert.notNull(userId,"userId must not be null.");
		try {
			SysLogInfo.OPER_TYPE type=SysLogInfo.OPER_TYPE.valueOf(operType);
			SysLogInfo.MODULE_LOG moduleLog=SysLogInfo.MODULE_LOG.manuscript;
			String operation="";
			if(type.equals(SysLogInfo.OPER_TYPE.login)||type.equals(SysLogInfo.OPER_TYPE.logout)){
				moduleLog=SysLogInfo.MODULE_LOG.manuscript_login;
			}
			switch (type) {
			case login:
				operation="登录";
				break;
			case logout:
				operation="登出";
				break;
			case create:
				operation="创建稿件";
				break;
			case save:
				operation="编辑稿件";
				break;
			case remove:
				operation="删除稿件";
				break;
			case recall:
				operation="撤回稿件";
				break;
			case audit:
				operation="审核稿件";
				break;
			default:
				break;
			}
			ManuscriptMember member=manuscriptMemberService.findMemberById(userId);
			SysLogInfo log=new SysLogInfo();
			log.setOperation(operation);
			log.setOperId(null);
			log.setUserId(userId);
			log.setLoginName(member==null?null:member.getLoginName());
			log.setOperDesc(content);
			log.setOperType(type);
			log.setModuleLog(moduleLog);
			log.setOperIp(ip);
			log.setOperTime(new Date());
			sysLogService.createSysLog(log);
		} catch (Exception e) {
		}
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	
	}

	@Override
	public Integer findManuscriptPraiseCount(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		return this.praiseService.findPraiseCountByOrgan(userId);
	}

	@Override
	public List<ServiceDto> findDataSourceListByUserId(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		List<ServiceDto> resultList=new ArrayList<ServiceDto>();
		List<Datasource> dataList=this.manuscriptIService.findServiceByCreateUserId(userId);
		if(CollectionUtils.isEmpty(dataList)){
			return resultList;
		}
		ServiceDto dto=null;
		for(Datasource data:dataList){
			dto=DataObjectTransformUtils.dataSourceToServiceDto(data);
			resultList.add(dto);
		}
		return resultList;
	}

	@Override
	public Map<String, Long> findManuscriptStateOfCountByUserId(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		Map<String, Long> map=new HashMap<String, Long>();
		Long waitSubmitAuditNum= this.manuscriptIService.findManuscriptCreateCount(userId,null,null,ManuStatus.waitSubmitAudit);
		Long waitAuditNum= this.manuscriptIService.findManuscriptCreateCount(userId,null,null,ManuStatus.waitAudit);
		Long auditPassNum= this.manuscriptIService.findManuscriptCreateCount(userId,null,null,ManuStatus.auditPass);
		Long auditNoPassNum= this.manuscriptIService.findManuscriptCreateCount(userId,null,null,ManuStatus.auditNoPass);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.waitSubmitAuditNum, waitSubmitAuditNum);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.waitAuditNum, waitAuditNum);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.auditPassNum,  auditPassNum);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.auditNoPassNum, auditNoPassNum);
		return map;
	}

	@Override
	public ManuscriptDto findCurrentUserOfDraft(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		return this.manuscriptIService.findLastManuscriptDtoByCreateUser(userId);
	}

	@Override
	public Integer findCurrentUserWhetherExpire(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		ManuscriptMember sysUser=this.manuscriptMemberService.findMemberById(userId);
		Date abortTime=sysUser.getOrgan().getAbortTime();
		Date curTime=new Date();
		if(abortTime!=null&&curTime.after(abortTime)){
			return DefinedExplain.RESULT_FLAG.EXPIRE;
		}else{
			Integer max=sysUser.getManuMaxCount();
		    Integer alrady=sysUser.getAlreadyManuCount();
		    if(max!=null&&max==alrady){
		    	return DefinedExplain.RESULT_FLAG.ERROR;
		    }else{
		    	return DefinedExplain.RESULT_FLAG.SUCCESS;
		    }
		}
	}

	@Override
	public ManuscriptDto findManuscriptById(Long id, Long userId) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		ManuscriptDto dto=this.manuscriptIService.findManuscriptDtoById(id);
		/*if(!dto.getCreateCmsUserId().equals(userId)){
			throw new RuntimeException("此稿件不属于该用户所管理.");
		}*/
		return dto;
	}

	@Override
	public ReplyDto findReplyFirstById(Long id, Long userId) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		ReplyInfo reply=this.manuscriptIService.findReplyFirstByCreateUserId(id, userId);
		ReplyDto dto=DataObjectTransformUtils.replyToReplyDto(reply);
		return dto;
	}

	@Override
	public List<ManuscriptServiceDto> findManuscriptServiceList(Long id) {
		Assert.notNull(id,"manuId must not be null.");
		List<ManuscriptService> list=this.manuscriptIService.findManuscriptServiceByManuId(id);
		List<ManuscriptServiceDto> resultList=new ArrayList<>();
		ManuscriptServiceDto dto=null;
		for(ManuscriptService ms:list){
			dto=DataObjectTransformUtils.manuscriptServiceToManuscriptServiceDto(ms);
			resultList.add(dto);
		}
		return resultList;
	}

	@Override
	public MemberServiceDto findMemberDataSource(Long userId, Long serviceId) {
		Assert.notNull(serviceId,"serviceId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		SendUserService sus=this.sendUserIService.findServiceUserByUserIdAndServiceId(userId, serviceId);
		MemberServiceDto dto=DataObjectTransformUtils.sendUserServiceToMemberServiceDto(sus);
		return dto;
	}

	@Override
	public List<ManuscriptDto> findManuscriptListOfSend(Long userId,
			String search, Long serviceId, String state, Pager pager) {
		Assert.notNull(userId,"userId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(state)?null:ManuStatus.valueOf(state);
		List<ManuscriptDto> list=this.manuscriptIService.findManuscriptCreateList(userId, search, serviceId, manuStatus, pager);
		return list;
	}
	
	@Override
	public Long findManuscriptCountOfSend(Long userId,
			String search, Long serviceId, String state) {
		Assert.notNull(userId,"userId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(state)?null:ManuStatus.valueOf(state);
		Long rowCount=this.manuscriptIService.findManuscriptCreateCount(userId, search, serviceId, manuStatus);
		return rowCount;
	}

	@Override
	public Integer removeManuscriptById(Long id, Long userId) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		if(this.manuscriptIService.findById(id).getCreateCmsUserId().equals(userId)){
			this.manuscriptIService.removeManuscript(id);
			return DefinedExplain.RESULT_FLAG.SUCCESS;
		}
		return DefinedExplain.RESULT_FLAG.NO_PERMISSION;
	}

	@Override
	public ManuscriptDto createManuscriptDraft(ManuscriptDto dto,
			String content, Long[] serviceIds, boolean isValid) {
		Assert.notNull(dto,"dto must not be null.");
		Assert.notNull(content,"content must not be null.");
		content=content.replace("\"/resource", "\"" +constantConfig.getWebsitePublishImgServer());
		String intro=CommonsUtil.getIntroFromContent(content, Integer.parseInt(constantConfig.getContentIntroLength()));
		Manuscript obj=DataObjectTransformUtils.manuscriptDtoToManuscript(dto);
		obj.setIntroduction(intro);
		obj.setContent(content);
		Manuscript pojo=this.manuscriptIService.createManuscriptDraft(obj, content, serviceIds, isValid);
		return DataObjectTransformUtils.manuscriptToManuscriptDto(pojo, null, null);
	}

	@Override
	public ManuscriptDto modifyManuscript(Long id, ManuscriptDto dto,
			Long[] serviceIds) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(dto,"dto must not be null.");
		String content=dto.getContent();
		content=content.replace("\"/resource", "\"" +constantConfig.getWebsitePublishImgServer());
		String intro=CommonsUtil.getIntroFromContent(content, Integer.parseInt(constantConfig.getContentIntroLength()));
		Manuscript obj=DataObjectTransformUtils.manuscriptDtoToManuscript(dto);
		obj.setIntroduction(intro);
		obj.setContent(content);
		this.manuscriptIService.modifyManuscript(obj, serviceIds);
		return DataObjectTransformUtils.manuscriptToManuscriptDto(obj, null, null);
	}

	@Override
	public Integer modifyManuscriptModifyTime(Long manuId, Date date) {
		Assert.notNull(manuId,"manuId must not be null.");
		this.manuscriptIService.modifyManuscriptModifyTime(manuId,  date==null?null:new Timestamp(date.getTime()));
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Integer modifyManuscriptState(Long id, Long userId, String state) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(state)?null:ManuStatus.valueOf(state);
		this.manuscriptIService.modifyManuscript(id,userId, manuStatus);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Integer createReply(Long manuId, String manuState, String content) {
		Assert.notNull(manuId,"manuId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(manuState)?null:ManuStatus.valueOf(manuState);
		this.manuscriptIService.createReplyEntity(manuId, manuStatus, content);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public String findShortTitleDataSource() {
		return constantConfig.getShortTitleIds();
	}

	@Override
	public Integer removeManuscriptDraft(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		this.manuscriptIService.removeManuscriptDraft(userId);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Map<String, Long> findAuditManuscriptStateOfCountByUserId(
			Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		Map<String, Long> map=new HashMap<>();
		Long waitAuditNum= this.manuscriptIService.findManuscriptAuditAllotCount(userId, null, null, ManuStatus.waitAudit);
		Long auditPassNum= this.manuscriptIService.findManuscriptAuditAllotCount(userId, null, null, ManuStatus.auditPass);
		Long auditNoPassNum= this.manuscriptIService.findManuscriptAuditAllotCount(userId, null, null, ManuStatus.auditNoPass);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.waitAuditNum, waitAuditNum);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.auditPassNum,  auditPassNum);
		map.put(DefinedExplain.MANU_STATECOUNT_KEY.auditNoPassNum, auditNoPassNum);
		return map;
	}

	@Override
	public List<ServiceDto> findCanAuditDataSourceList(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		List<Datasource> list=this.manuscriptIService.findServiceByAuditUserId(userId);
		List<ServiceDto> resultList=new ArrayList<>();
		ServiceDto dto=null;
		for(Datasource data:list){
			dto=DataObjectTransformUtils.dataSourceToServiceDto(data);
			resultList.add(dto);
		}
		return resultList;
	}

	@Override
	public List<ManuscriptDto> findUntreatedAuditManuscriptList(Long userId) {
		Assert.notNull(userId,"userId must not be null.");
		return this.manuscriptIService.findManuscriptAuditListUntreated(userId);
	}

	@Override
	public List<ManuscriptDto> findAuditManuscriptList(Long userId,
			Integer count) {
		Assert.notNull(userId,"userId must not be null.");
		return this.manuscriptIService.findAuditManuscript(userId, count);
	}

	@Override
	public Long findCurrentAuditCount(Long userId, boolean isLock) {
		Assert.notNull(userId,"userId must not be null.");
		return this.manuscriptIService.findWaitAuditManuscriptCount(userId, isLock);
	}

	@Override
	public List<ManuscriptDto> findManuscriptListOfAudit(Long userId,
			String search, Long serviceId, String state, Pager pager) {
		Assert.notNull(userId,"userId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(state)?null:ManuStatus.valueOf(state);
		List<ManuscriptDto> list=this.manuscriptIService.findManuscriptAuditList(userId, search, serviceId, manuStatus, pager);
		return list;
	}
	
	@Override
	public Long findManuscriptCountOfAudit(Long userId,
			String search, Long serviceId, String state) {
		Assert.notNull(userId,"userId must not be null.");
		ManuStatus manuStatus=org.springframework.util.StringUtils.isEmpty(state)?null:ManuStatus.valueOf(state);
		Long rowCount=this.manuscriptIService.findManuscriptAuditCount(userId, search, serviceId, manuStatus);
		return rowCount;
	}

	@Override
	public Integer updateManuscriptAuditLockTime(Long userId, String manuIds,
			Date date) {
		Assert.notNull(manuIds,"manuIds must not be null.");
		String[] manus=manuIds.split(",");
		for(String mid:manus){
			this.manuscriptIService.modifyManuscriptLockTime(Long.parseLong(mid), userId, date==null?null:new Timestamp(date.getTime()));
		}
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}

	@Override
	public Integer recallManuscript(Long id,Long userId) {
		Assert.notNull(id,"manuId must not be null.");
		Assert.notNull(userId,"userId must not be null.");
		this.manuscriptIService.modifyRecallManuscript(id,userId);
		return DefinedExplain.RESULT_FLAG.SUCCESS;
	}


}
