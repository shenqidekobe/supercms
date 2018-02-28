package com.dw.suppercms.domain.manu;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;

/**
 * 稿件批复记录表
 * */
@Entity
@Table(name = "T_REPLY")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReplyInfo extends IdentifiedEntity {
	
	private static final long serialVersionUID = -2823791386652658787L;

	private Long manuId; //稿件ID
	
	@Enumerated(EnumType.STRING)
	private ManuStatus manuStatus;//稿件的状态
	
	private String replyDesc;//
	
	private String createUserId;
	
	private String createUserName;
	
	
}
