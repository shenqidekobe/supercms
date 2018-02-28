package com.dw.suppercms.domain.data;

import javax.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * Data
 * <p>
 * holds the common fields for all model-tables
 * </p>
 * 
 * @author osmos
 * @date 2015年7月30日
 */
@Entity
@lombok.Data
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, of = {})
public class Data
		extends IdentifiedEntity {
	private static final long serialVersionUID = -302606997148234865L;

	/**
	 * DataOrigin
	 * <p>
	 * the data origin may be system, content-center ,publication , gather
	 * </p>
	 * 
	 * @author osmos
	 * @date 2015年7月28日
	 */
	public enum DataOrigin {
		SYSTEM, CONTENT_CENTER, PUBLICATION, GATHER
	}

	/**
	 * 
	 * CheckStatus
	 *  普通角色 看到 ORIGIN, FIRST_REFUSE
	 *  一审角色看到 FIRST,  SECOND_REFUSE
	 *  二审较色看到 SECOND
	 * @author osmos
	 * @date 2015年10月17日
	 */
	public enum CheckStatus {
		ORIGIN, FIRST_REFUSE, FIRST, SECOND_REFUSE, SECOND, PASS
	}
}
