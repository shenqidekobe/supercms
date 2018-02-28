package com.dw.suppercms.produce.listener.event;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.data.DataRecord;

/**
 * 生成文件的任务定义
 * */
@lombok.Data
public class MakeFileTask implements Serializable{
	
	private static final long serialVersionUID = 4851616140519601404L;
	
	private Long webId; //网站ID
	
	private Long fileId;//需要生成的对象文件ID{站点ID-栏目ID-自定义页ID}
	
	private Long parentId;//上级对象ID{栏目的上级ID-其他的上级ID}
	
	private String filePath;//文件地址{生成文件的相对路径地址包括文件名}
	
	private String fileName;//文件名称
	
	private Long templateId;//模版ID
	
	private String sourceName;//来源的名称{站点名称-栏目名称-自定义页名称}
	
	private MakeEventType makeEventType;//目标执行类型
	
	private DataRecord dataRecord;//文件的内容数据
	
	private Integer preCount=0;//生成前几个月的数据，默认0标识生成当月的，1表示前一个月
	
	private Map<String,Object> paramMap;//请求参数
	
	private Site site;//站点对象
	
	private Column column;//栏目对象
	
	private Custom custom;//自定义页对象
	
	public MakeFileTask(){}
	
	public MakeFileTask(MakeEventType makeEventType) {
		this.makeEventType = makeEventType;
	}

	public enum MakeEventType {
		INDEX("index", "首页"),
		LISTS("list", "列表页"),
		HISTORY("history","历史列表页"),
		CONTENT("content", "内容页"),
		CUSTOMS("custom", "自定义页"),
		DIRECTIVE("directive", "执行指令"),
		METHOD("method", "执行指令");
		MakeEventType(String key, String desc) {
			this.key = key;
			this.desc = desc;
		}
		@Getter
		private String key;
		@Getter
		private String desc;
	}
	
}
