package com.dw.suppercms.domain.security;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 用户数据源关系
 * */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class UserDataSource extends IdentifiedEntity{
	
	private static final long serialVersionUID = 2255164247013341369L;

	private Long userId;
	
	private Long datasourceId;
	
	public static UserDataSource newOf(Long userId, Long datasourceId) {
		UserDataSource userDatasource = UserDataSource.newOf();
		userDatasource.setUserId(userId);
		userDatasource.setDatasourceId(datasourceId);
		return userDatasource;
	}

}
