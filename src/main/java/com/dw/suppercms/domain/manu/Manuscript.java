package com.dw.suppercms.domain.manu;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.suppercms.support.ManuscriptDto.ManuStatus;

/**
 * 稿件实体
 * */
@Entity
@Table(name="t_manuscript")
@Data
@EqualsAndHashCode(callSuper = false)
public class Manuscript implements Serializable{

	private static final long serialVersionUID = 7471108102763322870L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long manuId;//稿件ID
	
	@Enumerated(EnumType.STRING)
	private ManuStatus manuStatus;//稿件状态
	
	private String writer;//作者
	
	private String title; //标题
	
	private String shortTitle;//短标题
	
	@Column(name = "`FROM`", length = 100)
	private String from;//来源
	
	private String editor;//责任编辑
	
	private String introduction;//简介
	
	@Lob
	private String content;//内容
	
	private Long createCmsUserId;//
	
	private String createUserId;//投稿人
	
	private String createOrganName;//投稿单位名
	
	private Timestamp createTime;//投稿时间
	
	private Timestamp submitAuditTime;//投稿人提交审核时间
	
	private Long auditUserId;//审核人
	
	private Timestamp auditTime;//审核时间
	
	private Long repealUserId; //撤销人
	
	private Timestamp repealTime;//撤销时间
	
	private Long updateUserId;//修改人
	
	private Timestamp updateTime;//修改时间
	
	private Timestamp removeTime;//删除时间
	
	private Long lockUserId;//锁定人ID
	
	private Timestamp lockTime;//锁定时间
	
	private Timestamp modifyTime;//审核未通过时修改的时间

}
