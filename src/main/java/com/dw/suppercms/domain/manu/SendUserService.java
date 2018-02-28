package com.dw.suppercms.domain.manu;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.data.Datasource;

/**
 * 投稿用户的数据源分配
 * */
@Entity
@Table(name = "T_SENDUSER_SERVICE")
@Data
@EqualsAndHashCode(callSuper = false)
public class SendUserService extends IdentifiedEntity{

	private static final long serialVersionUID = 6823522742683419553L;
	
	private Long serviceId;//数据源ID
	
	private Long userId; //投稿用户ID
	
	private boolean isAudit;//{1,0}
	
	@ManyToOne
	@JoinColumn(name = "serviceId", insertable = false, updatable = false)
	private Datasource datasource;
	
	@ManyToOne
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private ManuscriptMember member;
	
}
