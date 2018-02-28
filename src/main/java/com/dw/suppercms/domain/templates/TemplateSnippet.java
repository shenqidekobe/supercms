package com.dw.suppercms.domain.templates;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * 模版片段
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateSnippet extends IdentifiedEntity {

	private static final long serialVersionUID = 916230159386284028L;

	@NotNull
	@Size(max = 64)
	@Column(length = 64)
	private String snippetTag;

	@NotNull
	@Size(max = 32)
	@Column(length = 32)
	private String snippetName;

	@NotNull
	@Lob
	private String snippetCode;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "sort_id")
	private SysSortInfo sort;
	
	@Transient
	private Long sortId;

}
