package com.dw.suppercms.domain.plugin;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

import flexjson.JSONSerializer;

/**
 * 插件实体
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class PlugInInfo extends IdentifiedEntity{
	
	private static final long serialVersionUID = -2847081532584061068L;
	@NotNull
	private String plugName;    //插件名称
	@NotNull
	private String fieldName;//插件字段名
	@NotNull
	private String plugContent; //内容
	@Lob
	private String jsDefinition;//js定义
	@Lob
	private String jsDemo;//js定义demo说明
	private String attr1;
	private String updateUserid;
	

	@Transient
	public String getJsonString() {
		String serialize = new JSONSerializer().include("id").exclude("*").serialize(this);
		return serialize;
	}

}