package com.dw.suppercms.domain.system;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 系统日志记录
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SysLogInfo extends IdentifiedEntity {

	private static final long serialVersionUID = 5425429871904498540L;
	
	public static final String LOG_PRIMARY_ID=SysLogInfo.class.getName()+".ID";
	
	public static final String LOG_REQUEST_BODY=SysLogInfo.class.getName()+".BODY";


	/**日志操作类型{新增、修改、删除、禁用、启用、触发、发布、登录、登出、分配数据、分配权限、分配数据源
	 *         撤回、审核、投稿分配数据源、审稿分配权限
	 *         复制数据、移动数据、引用数据、置顶数据、取消置顶}
	 * */
	public enum OPER_TYPE {
		create, save, remove, disable, enable,trigger,publish,login,logout,allotData,allotSecurity,allotDataSource,
		recall,audit,allotSend,allotAudit,
		copy,move,ref,stick,unstick,dealTags,dataReview
	}
	
	//模块日志分位{系统日志、登录日志、投稿系统日志、投稿登录日志}
	public enum MODULE_LOG{
		system_module,system_login,manuscript,manuscript_login
	}

	@Enumerated(EnumType.STRING)
	private OPER_TYPE operType;//操作类型{新增-修改-删除-启用-禁用-*}
	
	@Enumerated(EnumType.STRING)
	private MODULE_LOG moduleLog;//按模块区分日志
	
	@Column(precision=20)
	private Long userId;  //操作用户ID
	
	@Column(length = 64)
	private String loginName;//用户名，冗余用户信息

	@Column(length = 64)
	private String operId;//操作对象的主键ID，用于跟踪数据

	@Column(length=128)
	private String operation;//操作名称
	
	@Column(length = 64)
	private String action;//方法名
	
	@Lob
	private String operParam;//操作请求参数

	@Column(length=1024)
	private String operDesc;//操作描述信息
	
	@Column(length = 128)
	private String operIp; //操作者IP

	@Temporal(TemporalType.TIMESTAMP)
	private Date operTime;  //操作时间

}
