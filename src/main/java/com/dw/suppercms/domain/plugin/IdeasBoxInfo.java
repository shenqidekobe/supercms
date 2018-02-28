package com.dw.suppercms.domain.plugin;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 意见箱实体
 * */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class IdeasBoxInfo extends IdentifiedEntity {

	private static final long serialVersionUID = -4126108027999092849L;
	
	private Long siteId; //站点来源
	
	private Integer status;//状态
	
	private String name;  //姓名
	
	private String sex;  //性别
	
	private String age; //年龄范围
	
	private String company;//所在单位
	
	private String duty; //职务
	
	private String jobTitle;//职称
	
	private String phone; //电话
	
	private String email; //邮箱
	
	private String content;//意见正文
	
}
