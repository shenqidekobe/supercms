package com.dw.suppercms.domain.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.modules.Column;

/**
 * 栏目数据关系
 * */
@Data
@Entity
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class ColumnData extends IdentifiedEntity {

	private static final long serialVersionUID = 5152642711938081301L;

	private Long columnId; // 栏目ID

	private Long dataId; // 数据ID

	private Long datasourceId; // 数据源ID

	private Long modelId;

	private Date produceDate;// 生成时间

	private boolean produceState;// 生成状态

	private boolean indexState;// 索引状态

	@ManyToOne
	@JoinColumn(name = "columnId", insertable = false, updatable = false)
	private Column column;

	public static ColumnData newOf(Long columnId, Long dataId, Long datasourceId, Long modelId) {
		ColumnData columnData = new ColumnData();
		columnData.setColumnId(columnId);
		columnData.setDataId(dataId);
		columnData.setModelId(modelId);
		columnData.setDatasourceId(datasourceId);
		columnData.setIndexState(false);
		columnData.setProduceState(false);
		return columnData;
	}

}
