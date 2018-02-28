package com.dw.suppercms.infrastructure.web.ui;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * TemplateCombotree
 * <p>
 * list templates map to sort tree.
 * </p>
 *
 * @author osmos
 * @date 2015年7月15日
 */
public class TemplateCombotree extends Combotree {

	// value equal 0 if sort, 1 if template
	@Getter
	@Setter
	private int type;

	public TemplateCombotree(Long id, String name,Long parentId, int type) {
		super(id, name, parentId);
		this.type = type;
	}
}
