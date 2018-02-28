package com.dw.suppercms.application.manu.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.DataService;
import com.dw.suppercms.application.manu.AuditAllotIService;
import com.dw.suppercms.application.manu.ManuscriptIService;
import com.dw.suppercms.application.manu.SendUserIService;
import com.dw.suppercms.application.modules.ColumnService;
import com.dw.suppercms.application.system.SysParamService;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.manu.AuditAllotService;
import com.dw.suppercms.domain.manu.AuditAllotServiceRepository;
import com.dw.suppercms.domain.manu.AuditAllotUser;
import com.dw.suppercms.domain.manu.AuditAllotUserRepository;
import com.dw.suppercms.domain.manu.Manuscript;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;
import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.dw.suppercms.domain.manu.ManuscriptOrganRepository;
import com.dw.suppercms.domain.manu.ManuscriptRepository;
import com.dw.suppercms.domain.manu.ManuscriptService;
import com.dw.suppercms.domain.manu.ManuscriptServiceRepository;
import com.dw.suppercms.domain.manu.ReplyInfo;
import com.dw.suppercms.domain.manu.ReplyRepository;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_KEY;
import com.dw.suppercms.infrastructure.common.ConstantConfig;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.webservice.support.DataObjectTransformUtils;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileService;
import com.dw.suppercms.produce.data.DataRecordTransformConvetor;
import com.dw.suppercms.produce.rule.CmsFileUtils;
import com.dw.suppercms.support.ColumnDto;
import com.dw.suppercms.support.ManuscriptDto;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;
import com.dw.suppercms.support.Pager;
import com.googlecode.genericdao.search.Search;

/**
 * 稿件信息service实现类
 * 
 * @author kobe
 * @date 2015-8-28
 * */
@ApplicationService
public class ManuscriptIServiceImpl  implements ManuscriptIService {
	
	@Resource
	private ManuscriptRepository manuscriptRepository;
	
	@Resource
	private ManuscriptServiceRepository manuscriptServiceRepository;
	
	@Resource
	private AuditAllotServiceRepository auditAllotServiceRepository;
	
	@Resource
	private AuditAllotUserRepository auditAllotUserRepository;
	
	@Resource
	private ReplyRepository replyRepository;
	
	@Resource
	private ManuscriptMemberRepository manuscriptMemberRepository;
	
	@Resource
	private ManuscriptOrganRepository manuscriptOrganRepository;
	
	@Resource
	private DatasourceRepository datasourceRepository;
	
	@Resource
	private ColumnDataRepostitory columnDataRepostitory;
	
	@Resource
	private ColumnService columnService;
	
	@Resource
	private SendUserIService sendUserIService;
	
	@Resource
	private DataService dataService;
	
	@Resource
	private ConstantConfig constantConfig;
	
	@Resource
	private SysParamService sysParamService;
	
	@Resource
	private MakeFileService makeFileService;
	
	@Resource(name="cmsFileUtils")
	private CmsFileUtils cmsFileUtils;
	
	private static Long USERID=100L;
	
	private static int intro_length=60;
	
	@Override
	public Manuscript createManuscriptDraft(Manuscript obj,String content,Long[] serviceIds,boolean isInterval){
		if(isInterval){
			this.validateArgs(content);
			obj.setManuStatus(ManuStatus.draft);//存草稿
		}else{
			this.validateArgs(obj.getTitle(),obj.getWriter(),content);
		}
		//检查该用户是否保存过草稿
		Manuscript msBean=this.manuscriptRepository.findLastManuscriptByCreateUser(obj.getCreateCmsUserId());
		if(msBean!=null){
			if(!isInterval){
				msBean.setManuStatus(obj.getManuStatus());//正式提交时，状态更改
			}
			String intro=CommonsUtil.getIntroFromContent(content, intro_length);
			msBean.setContent(content);
			msBean.setTitle(obj.getTitle());
			msBean.setShortTitle(obj.getShortTitle());
			msBean.setFrom(obj.getFrom());
			msBean.setWriter(obj.getWriter());
			msBean.setEditor(obj.getEditor());
			msBean.setIntroduction(intro);
			this.modifyManuscript(msBean,serviceIds);
			return msBean;
		}else{
			this.createManuscript(obj,content,serviceIds);
		}
		return obj;
	}

	@Override
	public Manuscript createManuscript(Manuscript obj,String content,Long[] serviceIds) {
		String intro=CommonsUtil.getIntroFromContent(content, intro_length);
		obj.setContent(content);
		obj.setIntroduction(intro);
		obj.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.manuscriptRepository.save(obj);
		//修改稿件后，保存稿件与数据源的关系
		if(serviceIds!=null&&serviceIds.length>0){
			ManuscriptService ms=null;
			for(Long serviceId:serviceIds){
				ms=new ManuscriptService();
				ms.setManuId(obj.getManuId());
				ms.setServiceId(serviceId);
				this.manuscriptServiceRepository.save(ms);
			}
		}
		return obj;
	}

	@Override
	public Manuscript modifyManuscript(Manuscript obj,Long[] serviceIds) {
		this.manuscriptRepository.save(obj);
		//修改稿件后，保存稿件与数据源的关系
		if(serviceIds!=null&&serviceIds.length>0){
			List<ManuscriptService> list=this.manuscriptServiceRepository.search(new Search().addFilterEqual("manuId", obj.getManuId()));
			if(!CollectionUtils.isEmpty(list)){
				for(ManuscriptService manuscriptService:list){
					this.manuscriptServiceRepository.remove(manuscriptService);
				}
			}
			
			ManuscriptService ms=null;
			for(Long serviceId:serviceIds){
				ms=new ManuscriptService();
				ms.setManuId(obj.getManuId());
				ms.setServiceId(serviceId);
				this.manuscriptServiceRepository.save(ms);
			}
		}
		return obj;
	}

	@Override
	public void removeManuscript(Long id) {
		this.validateArgs(id);
		this.modifyManuscript(id, null,ManuStatus.remove);
	}
	
	@Override
	public void removeManuscriptDraft(Long userId){
		this.validateArgs(userId);
		Manuscript obj=this.manuscriptRepository.findLastManuscriptByCreateUser(userId);
		if(obj!=null){
			this.manuscriptRepository.remove(obj);
		}
	}

	@Override
	public Manuscript modifyManuscript(Long id,Long userId, ManuStatus status) {
		this.validateArgs(id,status);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Manuscript obj=this.findById(id);
		obj.setManuStatus(status);
		if(status.equals(ManuStatus.waitAudit)){
			obj.setSubmitAuditTime(now);
		}else if(status.equals(ManuStatus.auditPass)||status.equals(ManuStatus.auditNoPass)){
			//审核通过或者不通过，都需要更新locktime
			obj.setAuditUserId(userId);
			obj.setAuditTime(now);
			obj.setLockUserId(null);
			obj.setLockTime(null);
			if(status.equals(ManuStatus.auditPass)){
				//审核通过的给单位用户已投稿数量累加1
				modifyMemberManuCount(obj.getCreateCmsUserId());
				
			}
		}else if(status.equals(ManuStatus.repeal)){
			obj.setRepealUserId(userId);
			obj.setRepealTime(now);
		}else if(status.equals(ManuStatus.remove)){
			obj.setRemoveTime(now);
		}
		this.manuscriptRepository.save(obj);
		//审核通过，则复制一份数据发到数据源表中
		if(status.equals(ManuStatus.auditPass)){
			List<ManuscriptService> msList=this.manuscriptServiceRepository.queryManuscriptServiceByManuId(id);
			if(!CollectionUtils.isEmpty(msList)){
				for(ManuscriptService ms:msList){
					this.saveDataService(obj.getWriter(), obj.getTitle(), obj.getShortTitle(),obj.getFrom(),obj.getEditor(),obj.getContent(), ms.getServiceId(), obj.getCreateCmsUserId(), id);
				}
			}
		}
		return obj;
	}
	
	@Override
	public void modifyManuscriptLockTime(Long manuId,Long lockUserId,Timestamp lockTime){
		this.validateArgs(manuId);
		Manuscript obj=this.findById(manuId);
		obj.setLockUserId(lockUserId);
		obj.setLockTime(lockTime);
		this.manuscriptRepository.save(obj);
	}
	
	
	public void modifyManuscriptModifyTime(Long manuId,Timestamp modifyTime){
		this.validateArgs(manuId);
		Manuscript obj=this.findById(manuId);
		obj.setModifyTime(modifyTime);
		this.manuscriptRepository.save(obj);
	}
	
	/**
	 * 更新会员投稿数量以及单位投稿数量
	 * */
	private void modifyMemberManuCount(Long memberId){
		ManuscriptMember member=this.manuscriptMemberRepository.find(memberId);
		if(member!=null){
			member.setAlreadyManuCount(member.getAlreadyManuCount()==null||member.getAlreadyManuCount()<0?1:member.getAlreadyManuCount()+1);
			this.manuscriptMemberRepository.save(member);
			
			ManuscriptOrgan organ=member.getOrgan();
			organ.setAlreadyManuCount(organ.getAlreadyManuCount()==null||organ.getAlreadyManuCount()<0?1:organ.getAlreadyManuCount()+1);
			this.manuscriptOrganRepository.save(organ);
		}
	}
	
	public void modifyRecallManuscript(final Long manuId,Long userId){
		this.validateArgs(manuId);
		//1、撤回稿件，设置稿件状态为审核不通过
		Manuscript manu=this.modifyManuscript(manuId, userId,ManuStatus.auditNoPass);
		//2、撤回审核通过的稿件，需要在用户数量上减1
		modifyMemberManuCount(manu.getCreateCmsUserId());
		final List<Map<String,Object>> dataMapList=dataService.findManuscriptFromData(null, manuId);
		//3、撤回稿件，删除已经发布的数据,先删除最终页文件，再删除数据
		String value = "true" ;//是否删除发布的数据
		String paramVal=this.sysParamService.findSysParamValByCodeAndKey(PARAM_CODE.manuscript, PARAM_KEY.recallIsRemoveFile.toString());
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(paramVal)){
			value=paramVal;
		}
		if(value.equals("true")){
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(Map<String,Object> map:dataMapList){
						Long id=Long.parseLong(map.get("id").toString());
						Long datasourceId=Long.parseLong(map.get("datasource_id").toString());
					    boolean flag=cmsFileUtils.removeFileFinalPage(id,datasourceId);//删除最终页
					    
					    if(flag)dataService.delete(id, datasourceId);//删除数据
					}
				}
			}).start();
		}else{
			for(Map<String,Object> map:dataMapList){
				Long id=Long.parseLong(map.get("id").toString());
				Long datasourceId=Long.parseLong(map.get("datasource_id").toString());
			    dataService.delete(id, datasourceId);//删除数据
			}
		}
		
	}

	@Override
	public Manuscript findById(Long id) {
		this.validateArgs(id);
		return this.manuscriptRepository.find(id);
	}

	@Override
	public Manuscript findLastManuscriptByCreateUser(Long createUserId) {
		this.validateArgs(createUserId);
		return this.manuscriptRepository.findLastManuscriptByCreateUser(createUserId);
	}
	
	@Override
	public ManuscriptDto findLastManuscriptDtoByCreateUser(Long createUserId) {
		this.validateArgs(createUserId);
		Manuscript obj= this.manuscriptRepository.findLastManuscriptByCreateUser(createUserId);
		if(obj==null){
			return null;
		}
		return this.manuscriptToManuscriptDto(obj);
	}

	
	@Override
	public Long findManuscriptCreateCount(Long createUserId, String searchKey,Long serviceId,ManuStatus status) {
		return this.manuscriptRepository.findManuscriptCreateCount(createUserId,searchKey,serviceId,status);
	}
	
	@Override
	public Long findManuscriptAuditCount(Long auditUserId, String searchKey,Long serviceId,ManuStatus status) {
		return this.manuscriptRepository.findManuscriptAuditCount(auditUserId,searchKey,serviceId, status);
	}
	

	@Override
	public List<ManuscriptDto> findManuscriptCreateList(Long createUserId,String searchKey,Long serviceId,
			ManuStatus status, Pager pager) {
		this.validateArgs(createUserId,status,pager);
		List<ManuscriptDto> result=new ArrayList<ManuscriptDto>();
		List<Manuscript> list=this.manuscriptRepository.findManuscriptCreateList(createUserId,searchKey,serviceId, status, pager);
		ManuscriptDto dto=null;
		for(Manuscript ms:list){
			dto=this.manuscriptToManuscriptDto(ms);
			if(status.equals(ManuStatus.auditPass)){
				Map<ColumnDto,String> columns=manuscriptToColumnMap(dto, createUserId, ms.getManuId());
				dto.setColumns(columns);
			}
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public List<ManuscriptDto> findManuscriptAuditList(Long auditUserId,String searchKey,Long serviceId,ManuStatus status,Pager pager){
		this.validateArgs(auditUserId,status,pager);
		List<ManuscriptDto> result=new ArrayList<ManuscriptDto>();
		List<Manuscript> list=this.manuscriptRepository.findManuscriptAuditList(auditUserId, searchKey, serviceId, status, pager);
		ManuscriptDto dto=null;
		for(Manuscript ms:list){
			dto=this.manuscriptToManuscriptDto(ms);
			if(status.equals(ManuStatus.auditPass)){
				Map<ColumnDto,String> columns=manuscriptToColumnMap(dto, null, ms.getManuId());
				dto.setColumns(columns);
			}
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public List<ManuscriptDto> findAuditManuscript(Long auditUserId,Integer count){
		this.validateArgs(auditUserId,count);
		List<ManuscriptDto> result=new ArrayList<ManuscriptDto>();
		List<AuditAllotIService> aasList=this.auditAllotServiceRepository.search(new Search().addFilterEqual("auditId", auditUserId));//获取分配的数据源
		Integer resultCount=0;
		String lockTime=this.getLockTime();
		List<Manuscript> listAll=new ArrayList<Manuscript>();
		//首先判断是否已经拿过数据,且未处理完的,发现有，则直接只拿未处理完成的数据
		List<Manuscript> untreatedList=this.manuscriptRepository.findManuscriptAuditListUntreated(auditUserId, lockTime);
		if(!CollectionUtils.isEmpty(untreatedList)){
			listAll.addAll(untreatedList);
		}else{
			//优先从数据源中获取待处理的数据，获取指定条数
			if(!CollectionUtils.isEmpty(aasList)){
				List<Manuscript> manuList=this.manuscriptRepository.findManuscriptAuditListByService(auditUserId, count, lockTime);
				resultCount=manuList.size();
				listAll.addAll(manuList);
				
			}
			List<AuditAllotUser> aauList=this.auditAllotUserRepository.search(new Search().addFilterEqual("auditId", auditUserId));//获取分配的单位用户
			//从数据源中获取数量不足，或者没有数据源配置的时候，则从单位用户获取
			if(!CollectionUtils.isEmpty(aauList)&&resultCount<count){
				List<Long> inManuIds=new ArrayList<Long>();//去重
				for(Manuscript ms:listAll){
					inManuIds.add(ms.getManuId());
				}
				Integer tempCount=count-resultCount;
				List<Manuscript> manuList=this.manuscriptRepository.findManuscriptAuditListByUser(auditUserId, tempCount, lockTime,inManuIds);
				listAll.addAll(manuList);
			}
		}
		ManuscriptDto dto=null;
		for(Manuscript ms:listAll){
			dto=this.manuscriptToManuscriptDto(ms);
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public List<ManuscriptDto> findManuscriptAuditAllotList(Long auditUserId,
			String searchKey, Long serviceId, ManuStatus status, Pager pager) {
		this.validateArgs(auditUserId,status);
		List<ManuscriptDto> result=new ArrayList<ManuscriptDto>();
		List<Manuscript> list=this.manuscriptRepository.findManuscriptAuditAllotList(auditUserId, searchKey, serviceId, status, pager);
		ManuscriptDto dto=null;
		for(Manuscript ms:list){
			dto=this.manuscriptToManuscriptDto(ms);
			if(status.equals(ManuStatus.auditPass)){
				Map<ColumnDto,String> columns=manuscriptToColumnMap(dto, null, ms.getManuId());
				dto.setColumns(columns);
			}
			result.add(dto);
		}
		return result;
	}

	@Override
	public Long findManuscriptAuditAllotCount(Long auditUserId,
			String searchKey, Long serviceId, ManuStatus status) {
		this.validateArgs(auditUserId,status);
		return this.manuscriptRepository.findManuscriptAuditAllotCount(auditUserId, searchKey, serviceId, status);
	}
	
	@Override
	public Long findWaitAuditManuscriptCount(Long auditUserId,boolean isNoLock){
		this.validateArgs(auditUserId);
		String lockTime=null;
		if(isNoLock){
			lockTime=getLockTime();
		}
		return this.manuscriptRepository.findWaitAuditManuscriptCount(auditUserId,lockTime);
	}
	
	@Override
	public 	List<ManuscriptDto> findManuscriptAuditListUntreated(Long auditUserId){
		this.validateArgs(auditUserId);
		List<Manuscript> untreatedList=this.manuscriptRepository.findManuscriptAuditListUntreated(auditUserId, getLockTime());
		ManuscriptDto dto=null;
		List<ManuscriptDto> result=new ArrayList<ManuscriptDto>();
		for(Manuscript ms:untreatedList){
			dto=this.manuscriptToManuscriptDto(ms);
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public ManuscriptDto findManuscriptDtoById(Long id){
		Manuscript obj=this.findById(id);
		return manuscriptToManuscriptDto(obj);
	}
	
	@Override
	public List<Datasource> findServiceByCreateUserId(Long createUserId){
		this.validateArgs(createUserId);
		List<Datasource> list=new ArrayList<Datasource>();
		List<SendUserService> suList=  this.sendUserIService.findServiceUserList(createUserId);
		if(!CollectionUtils.isEmpty(suList)){
			Datasource se=null;
			for(SendUserService su:suList){
				se=su.getDatasource();
				se.setIsAudit(su.getIsAudit());
				list.add(se);
			}
		}
		return list;
	}
	
	@Override
	public List<Datasource> findServiceByAuditUserId(Long auditUserId){
		this.validateArgs(auditUserId);
		List<Datasource> list=new ArrayList<Datasource>();
		List<AuditAllotService> serviceIds =  this.auditAllotServiceRepository.search(new Search().addFilterEqual("auditId", auditUserId));//先查找审核用户和数据源的关系
		List<AuditAllotUser> userIds =  this.auditAllotUserRepository.search(new Search().addFilterEqual("auditId", auditUserId));//先查找审核用户和投稿单位的关系
		Map<Long, Datasource> map=new HashMap<Long, Datasource>();
		if(!CollectionUtils.isEmpty(serviceIds)){
			Datasource se=null;
			for(AuditAllotService aas:serviceIds){
				se=aas.getDatasource();
				map.put(se.getId(),se);
			}
		}else if(!CollectionUtils.isEmpty(userIds)){
			for(AuditAllotUser aau:userIds){
				List<SendUserService> users=this.sendUserIService.findServiceUserList(aau.getUserId());
				Datasource se=null;
				for(SendUserService u:users){
					se=u.getDatasource();
					map.put(se.getId(),se);
				}
			}
		}
		if(!map.isEmpty()){
			for(Long key:map.keySet()){
				list.add(map.get(key));
			}
		}
		return list;
	}
	
	@Override
	public List<ManuscriptService> findManuscriptServiceByManuId(Long manuId){
		this.validateArgs(manuId);
		return this.manuscriptServiceRepository.queryManuscriptServiceByManuId(manuId);
	}
	
	
	
	
	@Override
	public ReplyInfo createReplyEntity(Long manuId,ManuStatus status,String replyDesc) {
		this.validateArgs(manuId,replyDesc);
		ReplyInfo obj=new ReplyInfo();
		obj.setCreateUserId(USERID.toString());
		obj.setCreateUserName(null);
		obj.setManuId(manuId);
		obj.setManuStatus(status);
		obj.setReplyDesc(replyDesc);
		this.replyRepository.save(obj);
		return obj;
	}
	
	@Override
	public ReplyInfo modifyReplyEntity(Long id,Long manuId,ManuStatus status,String replyDesc) {
		if(id==null){
			return this.createReplyEntity(manuId, status,replyDesc);
		}else{
			if(StringUtils.isEmpty(replyDesc)){
				this.removeReply(id);
			}else{
				ReplyInfo obj=this.replyRepository.find(id);
				obj.setReplyDesc(replyDesc);
				this.replyRepository.save(obj);
				return obj;
			}
		}
		return null;
	}

	@Override
	public void removeReply(Long id) {
		this.validateArgs(id);
		this.replyRepository.removeById(id);
	}

	@Override
	public ReplyInfo findReplyFirstByCreateUserId(Long manuId,
			Long createUserId) {
		this.validateArgs(manuId,createUserId);
		return this.replyRepository.findReplyFirstByCreateUserId(manuId,createUserId.toString());
	}

	@Override
	public List<ReplyInfo> findReplyEntityByManuId(Long manuId) {
		this.validateArgs(manuId);
		return this.replyRepository.findReplyByManuId(manuId);
	}
	
	/**
	 * 复制数据到数据源(保存作者，标题和正文),审核通过的时候
	 * */
	public void saveDataService(String writer,String title,String shortTitle,String from,String editor,String content,Long serviceId,Long createUserId,Long manuId){
		//调用dataService的create方法，具体字段的key参考SchemalEventListener的CREATE_TABLE_SQL
		Map<String, Object> valuesMap=new HashMap<>();
		String intro=CommonsUtil.getIntroFromContent(content, Integer.parseInt(constantConfig.getContentIntroLength()));
		valuesMap.put("datasource_id", serviceId);
		valuesMap.put("manu_id", manuId);
		valuesMap.put("title", title);
		valuesMap.put("shortTitle", shortTitle);
		valuesMap.put("source", from);
		valuesMap.put("author", writer);
		valuesMap.put("editor", editor);
		valuesMap.put("introduce", intro);
		valuesMap.put("content", content);
		valuesMap.put("create_user_id", createUserId);
		dataService.create(valuesMap);
		//TODO:是否是直接通过，生成发布
		new Thread(new Runnable() {
			public void run() {
				Datasource datasource=datasourceRepository.find(serviceId);
				Set<Column> columns=datasource.getColumns();
				CompletionService<MakeFileResult> completionService =makeFileService.makeContentFile(
						DataRecordTransformConvetor.dataMapToDataRecord(valuesMap, constantConfig.getWebsitePublishImgServer()), new ArrayList<Column>(columns));
				int length=columns.size();
				for (int i = 0; i < length; i++) {
					MakeFileResult result = null;
					try {
						Future<MakeFileResult> future = completionService.take();
						result = future.get();
						if(result.getMakeResult().equals(MakeFileResult.MAKE_RESULT.SUCCESS)){
							//发布到public目录
							String src = result.getFilePath();
							String desc = src.replace(Module.MAKE_DIR, Module.PUB_DIR);
							new File(desc).mkdirs();
							CmsFileUtils.sysncDirs(src, desc);
						}
					}catch(Exception e){}
				}
			}
		}).start();
		
		
	
	}
	
	
	/**
	 * 稿件转换成对应的栏目map
	 * */
	private Map<ColumnDto,String> manuscriptToColumnMap(ManuscriptDto dto,Long userId,Long manuId){
		Map<ColumnDto,String> columns=new LinkedHashMap<ColumnDto, String>();
		List<ManuscriptService> mslist=this.manuscriptServiceRepository.search(new Search().addFilterEqual("manuId",manuId));
		for(ManuscriptService msl:mslist){
			if(userId!=null){
				SendUserService su=this.sendUserIService.findServiceUserByUserIdAndServiceId(userId, msl.getServiceId());
				dto.setAudit(su.getIsAudit());
			}
			Set<Column> clist = msl.getDataSource().getColumns();
			ColumnDto columnDto=null;
			for(Column ce:clist){
				String url="";
				List<Map<String,Object>> dataMapList=this.dataService.findManuscriptFromData(ce.getDatasourceId(),manuId);
				if(!dataMapList.isEmpty()){
					Map<String,Object> dataMap=dataMapList.get(0);
					Object id=dataMap.get("id");
					Timestamp createTime=(Timestamp) dataMap.get("create_time");
					ColumnData cd=this.columnDataRepostitory.findColumnDataByDataIdAndDatasourceId(Long.parseLong(id.toString()), ce.getDatasourceId());
					if(cd!=null&&cd.getProduceState()){
						url= ce.getSite().getProductDomain() + ce.getContentWebPath(id.toString(), createTime);
					}else{
						url= "";	
					}
				}
				columnDto=DataObjectTransformUtils.columnToColumnDto(ce);
				columns.put(columnDto, url);
			}
		}
		return columns;
	}
	
	
	/**
	 * 稿件转换dto
	 * */
	private ManuscriptDto manuscriptToManuscriptDto(Manuscript obj){
		List<ManuscriptService> msList=this.manuscriptServiceRepository.search(new Search().addFilterEqual("manuId", obj.getManuId()));
		List<ReplyInfo> replyList=this.findReplyEntityByManuId(obj.getManuId());//稿件批复记录
		ManuscriptDto dto=DataObjectTransformUtils.manuscriptToManuscriptDto(obj, msList, replyList);
		return dto;
	}
	
	
	/**
	 * 获取系统配置的锁定时间
	 * */
	private String getLockTime(){
		Integer expire=10;
		String value = constantConfig.getAuditLockTime(); // 过期时间设置
		if(!org.apache.commons.lang3.StringUtils.isEmpty(value)){
			try {
				expire=Integer.parseInt(value);	
			} catch (Exception e) {
			}
		}
		Calendar calendar=Calendar.getInstance(); 
		calendar.add(Calendar.MINUTE, -expire);//过期时间设置(精确至分钟)
		return CommonsUtil.formatDateToString(calendar.getTime());
	}
	
	
	
	
	private void validateArgs(Object... args) {
		for (Object temp : args) {
			if (temp==null) {
				throw new BusinessException("对象不能为空");
			}
			if (temp instanceof String) {
				if (StringUtils.isEmpty(temp.toString())) {
					throw new BusinessException("参数不能为空");
				}
			}
		}
	}

}
