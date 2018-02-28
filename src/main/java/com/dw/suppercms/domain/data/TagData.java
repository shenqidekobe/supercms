package com.dw.suppercms.domain.data;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 
 * TagData
 *
 * @author osmos
 * @date 2015年9月8日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class TagData
		extends IdentifiedEntity {

	private static final long serialVersionUID = 7268332306998523576L;

	private Long tagId;
	private Long dataId;
	private Long datasourceId;
	private Long modelId;

	public static TagData newOf(Long tagId, Long dataId, Long datasourceId, Long modelId) {
		TagData result = TagData.newOf();
		result.setTagId(tagId);
		result.setDataId(dataId);
		result.setDatasourceId(datasourceId);
		result.setModelId(modelId);
		return result;
	}
}
