package com.dw.suppercms.infrastructure.web.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Combotree {
	@Getter
	@Setter
	private Long id;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private Long parentId;
	@Getter
	@Setter
	private List<Combotree> childrens;
	
	
	
	public Combotree(Long id, String name, Long parentId) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.childrens = new ArrayList<Combotree>();
	}

}
