package com.dw.suppercms.domain.data;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.annotation.StringEnumeration;
import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.data.Data.DataOrigin;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import flexjson.JSON;

/**
 * 
 * Datasource
 *
 * @author osmos
 * @date 2015年7月28日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Datasource
		extends IdentifiedEntity {
	private static final long serialVersionUID = 5679834002512322765L;

	// --------------------------------------------enums
	public enum PassStatus {
		NO, ONE, TWO
	}

	// --------------------------------------------simple Fiels

	// name of the datasource
	@NotNull
	@Size(max = 32)
	private String title;

	@NotNull
	@Size(max = 64)
	private String description;

	// come from of datasource
	@NotNull
	@Size(max = 16)
	@StringEnumeration(enumClass = DataOrigin.class)
	private String origin;

	//  check pass status
	@NotNull
	@Size(max = 3)
	@StringEnumeration(enumClass = PassStatus.class)
	private String passStatus;

	// --------------------------------------------associate Fiels

	// associated sort id
	@NotNull
	private Long sortId;

	// associated model id
	@NotNull
	private Long modelId;

	// associate sort object
	@ManyToOne
	@JoinColumn(name = "sortId", insertable = false, updatable = false)
	private SysSortInfo sort;

	// associate sort object
	@ManyToOne
	@JoinColumn(name = "modelId", insertable = false, updatable = false)
	private Model model;
	
	@JSON(include=false)
	@JsonIgnore
	@OneToMany(mappedBy = "datasource")
	private Set<Column> columns;

	@Transient
	private boolean selected;
	@Transient
	private boolean isAudit;
	// --------------------------------------------own implements

	/**
	 * build a new datasource
	 * 
	 * @param datasource holds the new building datasource state
	 * @return the built datasource
	 */
	public static Datasource newOf(Datasource datasource) {
		Datasource newDatasource = Datasource.newOf();
		newDatasource.setTitle(datasource.getTitle());
		newDatasource.setDescription(datasource.getDescription());
		newDatasource.setModelId(datasource.getModelId());
		newDatasource.setOrigin(datasource.getOrigin());
		newDatasource.setSortId(datasource.getSortId());
		newDatasource.setPassStatus(datasource.getPassStatus());
		return newDatasource;
	}

	/**
	 * alter datasource state
	 * 
	 * @param datasource holds the altering datasource state
	 * @return the altered datasource
	 */
	public Datasource alterOf(Datasource datasource) {
		setTitle(datasource.getTitle());
		setDescription(datasource.getDescription());
		setModelId(datasource.getModelId());
		setOrigin(datasource.getOrigin());
		setSortId(datasource.getSortId());
		setPassStatus(datasource.getPassStatus());
		return this;
	}
}
