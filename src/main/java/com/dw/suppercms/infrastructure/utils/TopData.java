package com.dw.suppercms.infrastructure.utils;

import java.io.Serializable;

import lombok.Data;

@Data
public class TopData implements Serializable{

	private static final long serialVersionUID = -6462896411743345187L;
	private String organName;//单位
	private String title;
	private String link;
	private Integer num;
	
}
