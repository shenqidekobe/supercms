package com.dw.suppercms.infrastructure.web.ui;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Datatable {

	@Getter
	@Setter
	private int draw;
	@Getter
	@Setter
	private int recordsTotal;
	@Getter
	@Setter
	private int recordsFiltered;
	@Getter
	@Setter
	private List<?> data;
}
