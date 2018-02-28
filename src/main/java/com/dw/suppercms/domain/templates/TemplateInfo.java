package com.dw.suppercms.domain.templates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.system.SysSortInfo;

/**
 * 模版信息
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateInfo extends IdentifiedEntity {

	private static final long serialVersionUID = 2724770603740886365L;

	public enum TEMPLATE_TYPE {
		index, list, content,custom
	}

	@NotNull
	@Size(max = 64)
	@Column(length = 64)
	private String templateName;

	@NotNull
	@Lob
	private String templateCode;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TEMPLATE_TYPE templateType;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "sort_id")
	private SysSortInfo sort;
	
	@Transient
	private Long sortId;
	
}
