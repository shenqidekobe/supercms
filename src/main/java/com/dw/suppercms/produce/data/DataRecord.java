package com.dw.suppercms.produce.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 生成所需的数据对象
 * */
@lombok.Data
public class DataRecord implements Serializable{

	private static final long serialVersionUID = 2738709616547839919L;
	
	private String id; //数据ID
	private Long dataSourceId;//数据源ID
	private Long modelId;//模型ID
	
	private String title;  //数据标题
	private String shortTitle;//数据短标题
	private String intro; //数据简介
	private String content; //数据内容
	
	private String from; //数据来源
	private String writer; //数据作者
	private String editor; //责任编辑
	
	private String headerPicRef;//图片引用地址
	private String urlRef;//url引用地址
	
	
	private Date time; //数据创建时间
	private Date publishTime;//数据发布时间
	
	private Map<String, Object> extendFiledMap;//扩展字段map
	
}
