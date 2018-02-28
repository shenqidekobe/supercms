package com.dw.suppercms.infrastructure.web.ui;

import lombok.Getter;
import lombok.Setter;

import com.dw.suppercms.domain.modules.Module.ModuleType;

public class ColumnCombotree extends Combotree {

	@Getter
	@Setter
	private Long siteId;
	@Getter
	@Setter
	private Long datasourceId;
	@Getter
	@Setter
	private String datasourceName;
	@Getter
	@Setter
	private ModuleType type;
	@Getter
	@Setter
	private boolean valid;

	public ColumnCombotree(Long id, String name, Long parentId, Long siteId, Long datasourceId,ModuleType type, boolean valid) {
		super(id, name, parentId);
		this.siteId = siteId;
		this.type = type;
		this.datasourceId = datasourceId;
		this.valid = valid;
	}
}
