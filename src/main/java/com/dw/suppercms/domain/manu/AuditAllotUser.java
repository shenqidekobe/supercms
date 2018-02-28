package com.dw.suppercms.domain.manu;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 审核分配单位用户关系
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class AuditAllotUser extends IdentifiedEntity{

	private static final long serialVersionUID = -2479459377251866382L;
	
	private Long auditId;//审核人ID
	
	private Long userId;//数据源ID
	
	private Long createUserId;//创建人ID
	
	@ManyToOne
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private ManuscriptMember member;
	

}
