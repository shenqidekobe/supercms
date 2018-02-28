package com.dw.suppercms.domain.manu;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.data.Datasource;

/**
 * 审核分配数据源关系
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class AuditAllotService extends IdentifiedEntity{

	private static final long serialVersionUID = -2479459377251866382L;
	
	private Long auditId;//审核人ID
	
	private Long serviceId;//数据源ID
	
	private Long createUserId;//创建人ID
	
	@ManyToOne
	@JoinColumn(name = "serviceId", insertable = false, updatable = false)
	private Datasource datasource;
	
	

}
