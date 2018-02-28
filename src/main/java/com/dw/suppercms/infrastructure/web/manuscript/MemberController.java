package com.dw.suppercms.infrastructure.web.manuscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.DatasourceService;
import com.dw.suppercms.application.manu.AuditAllotIService;
import com.dw.suppercms.application.manu.ManuscriptMemberService;
import com.dw.suppercms.application.manu.SendUserIService;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_STATE;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_TYPE;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

import flexjson.JSONDeserializer;

/**
 * 投稿用户处理
 * @author kobe
 * */
@RestController
@RequestMapping("/manuscripts")
public class MemberController extends BaseController{
	
	@Resource
	private ManuscriptMemberService manuscriptMemberService;
	
	@Resource
	private AuditAllotIService auditAllotIService;
	
	@Resource
	private SendUserIService sendUserIService;
	
	@Resource
	private DatasourceService datasourceService;
	
	/**
	 * 验证登录名是否唯一
	 * */
	@RequestMapping(value = "/members/validateLoginName", method = { RequestMethod.GET })
	public boolean validateLoginName(Long id, String loginName) {
		boolean valid = true;
		if (id == null) {
			valid = manuscriptMemberService.validateLoginName(loginName);
		} else {
			ManuscriptMember member = manuscriptMemberService.findMemberById(id);
			if (!member.getLoginName().equals(loginName)) {
				valid = manuscriptMemberService.validateLoginName(loginName);
			}
		}
		return valid;
	}
	
	/**
	 * 验证用户电话号码是否唯一
	 * */
	@RequestMapping(value = "/members/validatePhone", method = { RequestMethod.GET })
	public boolean validatePhone(Long id, String phone) {
		boolean valid = true;
		if (id == null) {
			valid = manuscriptMemberService.validatePhone(phone);
		} else {
			ManuscriptMember member = manuscriptMemberService.findMemberById(id);
			if (!member.getPhone().equals(phone)) {
				valid = manuscriptMemberService.validatePhone(phone);
			}
		}
		return valid;
	}
	
	/**
	 * 验证用户邮箱是否唯一
	 * */
	@RequestMapping(value = "/members/validateEmail", method = { RequestMethod.GET })
	public boolean validateEmail(Long id, String email) {
		boolean valid = true;
		if (id == null) {
			valid = manuscriptMemberService.validateEmail(email);
		} else {
			ManuscriptMember member = manuscriptMemberService.findMemberById(id);
			if (!member.getEmail().equals(email)) {
				valid = manuscriptMemberService.validateEmail(email);
			}
		}
		return valid;
	}
	
	/**
	 * 获取所有信息
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.GET })
	public List<ManuscriptMember> all() {
		return manuscriptMemberService.all();
	}
	
	/**
	 * 获取审核员所有的投稿用户
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.GET }, params = { "auditMembers" })
	public List<ManuscriptMember> auditMemberList(Long auditId){
		List<ManuscriptMember> all=this.manuscriptMemberService.findMemberListByType(MEMBER_TYPE.SEND, MEMBER_STATE.NORMAR);
		List<ManuscriptMember> already=this.auditAllotIService.findAllotUserListByAudit(auditId);
		for(ManuscriptMember mem:all){
			if(already.contains(mem)){
				mem.setSelected(true);
			}
		}
		return all;
	}
	
	
	/**
	 * 获取审核员所有的数据源列表
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.GET }, params = { "auditDatasources" })
	public List<Datasource> auditDatasourceList(Long auditId){
		List<Datasource> all=this.datasourceService.all();
		List<Datasource> already= this.auditAllotIService.findAllotServiceListByAudit(auditId);
		for(Datasource mem:all){
			if(already.contains(mem)){
				mem.setSelected(true);
			}
		}
		return all;
	}
	
	/**
	 * 获取投稿员所有的数据源列表
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.GET }, params = { "sendDatasources" })
	public List<Datasource> sendDatasources(Long sendId){
		List<Datasource> all=this.datasourceService.all();
		if(sendId!=null){
			List<SendUserService> already= this.sendUserIService.findServiceUserList(sendId);
			for(Datasource mem:all){
				for(SendUserService userService:already){
					if(userService.getServiceId().equals(mem.getId())){
						mem.setSelected(true);
						mem.setIsAudit(userService.getIsAudit());
						break;
					}
				}
			}
		}
		return all;
	}


	/**
	 * 用户列表查询
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(String memberType,String state,int draw, int start, int length) {
		MEMBER_TYPE type=StringUtils.isEmpty(memberType)?null:MEMBER_TYPE.valueOf(MEMBER_TYPE.class,memberType);
		MEMBER_STATE status=StringUtils.isEmpty(state)?null:MEMBER_STATE.valueOf(MEMBER_STATE.class,state);
		SearchResult<ManuscriptMember> data = manuscriptMemberService.paginateAll(type,status,start,length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	/**
	 * 获取单个用户信息
	 * */
	@RequestMapping(value = "/members/{id}", method = { RequestMethod.GET })
	public ManuscriptMember id(@PathVariable Long id) {
		return manuscriptMemberService.findMemberById(id);
	}

	/**
	 * 创建用户
	 * */
	@RequestMapping(value = "/members", method = { RequestMethod.POST })
	@SystemLog(operation="创建投稿用户",operType=OPER_TYPE.create)
	@Description("创建投稿用户")
	@RequiresPermissions({ "app.manuscripts.member.create" })
	public ManuscriptMember create(@RequestBody @Valid ManuscriptMember member) {
		ManuscriptMember newManuscriptMember = manuscriptMemberService.create(member);
		requestBodyAsLog(newManuscriptMember);
		return newManuscriptMember;
	}

	/**
	 * 编辑用户
	 * */
	@RequestMapping(value = "/members/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑投稿用户",operType=OPER_TYPE.save)
	@Description("修改投稿用户")
	@RequiresPermissions({ "app.manuscripts.member.save" })
	public ManuscriptMember save(@RequestBody @Valid ManuscriptMember member, BindingResult br) {
		manuscriptMemberService.update(member.getId(), member);
		
		requestBodyAsLog(member);
		return member;
	}

	/**
	 * 删除用户
	 * */
	@RequestMapping(value = "/members/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除投稿用户",operType=OPER_TYPE.remove)
	@Description("删除投稿用户")
	@RequiresPermissions({ "app.manuscripts.member.remove" })
	public void remove(@PathVariable Long id) {
		manuscriptMemberService.delete(id);
		
	}
	
	/**
	 * 投稿用户的投稿栏目分配
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/members", method = { RequestMethod.POST },params={"sendAllot"})
	@SystemLog(operation="投稿用户分配数据源",operType=OPER_TYPE.allotSend)
	@Description("分配数据源")
	@RequiresPermissions({ "app.manuscripts.member.sendAllot" })
	public void sendAllot(@RequestBody Map map){
		Integer memberId=(Integer) map.get("memberId");
		if(memberId==null){
			throw new BusinessException("未设置投稿员ID");
		}
		String json=(String) map.get("json");
		
		List lists=new JSONDeserializer<ArrayList>().deserialize(json);
		List<SendUserService> suLists=new ArrayList<>();
		SendUserService sendUserService=null;
		for(Object obj:lists){
			Long serviceId=null;
			Boolean isAudit=false;
			HashMap<String, Object> hash=(HashMap<String, Object>) obj;
			for(String key:hash.keySet()){
				if(key.equals("serviceId")){
					serviceId=Long.parseLong(hash.get(key).toString());
				}
				if(key.equals("isAudit")){
					isAudit=Boolean.parseBoolean(hash.get(key).toString());
				}
			}
			sendUserService=new SendUserService();
			sendUserService.setServiceId(serviceId);
			sendUserService.setIsAudit(isAudit);
			suLists.add(sendUserService);
		}
		Long userId=Long.parseLong(memberId.toString());
		this.sendUserIService.removeServiceUser(userId);
		this.sendUserIService.createServiceUser(userId,suLists);
	
		requestBodyAsLog(map);
	}
	
	
	/**
	 *审稿分配
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/members", method = { RequestMethod.POST },params={"auditAllot"})
	@SystemLog(operation="审稿用户分配审稿权限",operType=OPER_TYPE.allotAudit)
	@Description("分配审稿权限")
	@RequiresPermissions({ "app.manuscripts.member.auditAllot" })
	public void auditeAllot(@RequestBody Map map){
		Integer memberId=(Integer) map.get("auditUserId");
		if(memberId==null){
			throw new BusinessException("未设置审核员ID");
		}
		Long auditUserId=Long.parseLong(memberId.toString());
		this.auditAllotIService.removeAuditAllot(auditUserId);
		Object memMap= map.get("memberIds");
		Object dsuMap= map.get("dataSourceIds");
		Object[] memberIds=null;
		Object[] dataSourceIds=null;
		if(memMap!=null){
			ArrayList<Long> mArrays=(ArrayList<Long>) memMap;
			memberIds=mArrays.toArray();
			auditAllotIService.createAuditAllotUser(auditUserId, memberIds);
		}
		if(dsuMap!=null){
			ArrayList<Long> dArrays=(ArrayList<Long>) dsuMap;
			dataSourceIds=dArrays.toArray();
			auditAllotIService.createAuditAllotService(auditUserId, dataSourceIds);
		}
		
		requestBodyAsLog(map);
	}

}
