package com.dw.suppercms.domain.manu;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 投稿单位
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false,exclude={"members"})
public class ManuscriptOrgan implements Serializable{
	
	private static final long serialVersionUID = 3947670438406118189L;
	
	public enum ORGAN_LEVEL{
		一,二,三,四,五,六,七,八,九,十
	}
	
	@Id
	@GeneratedValue
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	@NotNull
	private String organName;//单位名称
	private String platformOrganId;//学习平台单位ID
	@NotNull
	@Enumerated(EnumType.STRING)
	private ORGAN_LEVEL organLevel;//单位用户的级别{1、2、3、4、5级}
	@NotNull
	private Integer manuMaxCount;//单位用户最大投稿数量
	private Integer alreadyManuCount=0;//单位已投稿数量
	@Temporal(TemporalType.DATE)
	private Date abortTime; //单位投稿截止日期 
	private String description;//描述
	
	@JsonIgnore
	@OneToMany(mappedBy = "organ")
	private Set<ManuscriptMember> members;

}
