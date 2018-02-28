package com.dw.suppercms.domain.manu;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 投稿会员
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false,exclude={"organ","selected"})
public class ManuscriptMember implements Serializable{
	
	private static final long serialVersionUID = -2977559612520461796L;
	
	public enum MEMBER_TYPE{
		SEND,AUDIT
	}
	public enum MEMBER_STATE{
		NORMAR,LOCK,DELETE
	}
	
	@Id
	@GeneratedValue
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	private String memberName;  //用户名称
	private String loginName; //用户登录名
	private String password;  //登录密码
	@Enumerated(EnumType.STRING)
	private MEMBER_STATE state=MEMBER_STATE.NORMAR;   //用户状态   正常/锁定/删除
	@Enumerated(EnumType.STRING)
	private MEMBER_TYPE memberType; //用户类型 投稿or审稿
	
	private Integer manuMaxCount;//用户最大投稿数量
	private Integer alreadyManuCount;//已投稿数量
	private Boolean isAudit;//是否需要审核
	
	private String phone;     //用户电话
	private String email;     //用户邮箱
	private String lastLoginip;//最后登录IP
	private Date lastLogintime; //最后登录IP
	private String remark;       //用户备注
	
	@NotNull
	private Long organId;
	
	@ManyToOne
	@JoinColumn(name = "organId", insertable = false, updatable = false)
	private ManuscriptOrgan organ;
	
	@Transient
	private boolean selected;

}
