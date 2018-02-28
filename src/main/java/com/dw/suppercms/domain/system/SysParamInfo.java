package com.dw.suppercms.domain.system;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 系统参数对象
 * */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class SysParamInfo extends IdentifiedEntity{
	
	private static final long serialVersionUID = 4716601932514375066L;

	//参数code定义{投稿参数、生成参数、系统参数}
	//{recallIsRemoveFile、isProduceHistory,systemEmail}
	public enum PARAM_CODE{
		manuscript,produce,system
	}
	
	//参数key定义
	public enum PARAM_KEY{
		recallIsRemoveFile,
		isProduceHistory,
		systemEmail
	}
	
	@Enumerated(EnumType.STRING)
	private PARAM_CODE paramCode;
	
	private String paramKey;
	
	private String paramVal;
	
	private String description;

}
