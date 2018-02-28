package com.dw.suppercms.domain.manu;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.data.Datasource;

/**
 * 稿件数据源关系
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class ManuscriptService extends IdentifiedEntity{

	private static final long serialVersionUID = -7294571203824930071L;
	
	private Long manuId;  //稿件ID
	
	private Long serviceId;//数据源ID
	
	@ManyToOne
	@JoinColumn(name = "serviceId", insertable = false, updatable = false)
	private Datasource dataSource;
	
}
