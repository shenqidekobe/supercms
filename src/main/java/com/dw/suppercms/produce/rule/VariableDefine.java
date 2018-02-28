package com.dw.suppercms.produce.rule;


/**
 * freemarker模版中的自定义变量名定义
 * */
public class VariableDefine {
	
	
	/************************common variable****************************/
	public final static String variable_obj="obj";//当前数据对象
	public final static String variable_list="list";//当前数据集合
	public final static String variable_sid="sid";//当前站点ID
	public final static String variable_cid="cid";//当前栏目ID
	public final static String variable_coid="coid";//当前栏目关联的栏目ID
	public final static String variable_cname="cname";//当前栏目名
	public final static String variable_pid="pid"; //当前栏目上级ID
	public final static String variable_clink="clink";//当前栏目的相对地址
	public final static String variable_history="history";//生成历史列表文件
	public final static String variable_start="startTime";//开始时间
	public final static String variable_end="endTime";//结束时间
	public final static String variable_pagesize="pageSize";//分页大小
	
	
	
	/*************************custom directive**************************/
	public final static String directive_asign="asign";
	public final static String directive_cons="cons";  //自定义模版指令
	public final static String directive_page="page";  //分页指令
	public final static String directive_plugin="plugin";//插件指令
	public final static String directive_praiseTop="praiseTop";//点赞排行榜指令
	public final static String directive_hitsTop="hitsTop"; //点击量排行榜指令
	public final static String directive_rec="rec";  //数据指令
	public final static String directive_video="video";//视频指令
	public final static String directive_stat="stat";
	
	/*************************record directive params*********************/
	public final static String pading_pagethNum="pagethNum";//总页数
	public final static String pading_rowNum="rowNum";//总记录数量
	public final static String pading_pageth="pageth";//当前页
	public final static String pading_first="first";//第一页
	public final static String pading_prev="prev";//上一页
	public final static String pading_next="next";//下一页
	public final static String pading_last="last";//最后一页
	public final static String pading_pageCount="pageCount";//页数
	public final static String pading_pagethsHrefList="pagethsHrefList";//页面的链接地址集合
	public final static String pading_pagethNumList="pagethNumList";//页面的页数集合
	public final static String pading_href="href";//页的地址
	
	
	
	/*************************template select prefix*********************/
	public final static String template_prefix_templateinfo="TemplateInfo#";//模版对象
	public final static String template_prefix_custominfo="Custom#";//自定义页对象
	
	
	/*************************date format partten***********************/
	public final static String date_format_month="yyyy-MM";
	public final static String date_format_day="yyyy-MM-dd";
	public final static String date_format_hour="yyyy-MM-dd HH";
    public final static String date_format_minute="yyyy-MM-dd HH:mm";
    public final static String date_format_second="yyyy-MM-dd HH:mm:ss";
    
    
    /**********************data directive mapQueryParams*****************/
    public final static String map_params_query_produce_key="produce";//生成X数据标识
    public final static String map_params_query_stick_key="stick";//置顶标识
    public final static String map_params_query_tag_key="tags";//标签标识
    public final static String map_params_query_exclude_key="excludeId";//排除数据ID
    
    public final static String map_params_query_produceall="produceAll";//查询所有{栏目中刷新页面时}
    public final static String map_params_query_produced="produced";//查询已发布的数据{生成列表页时}
    public final static String map_params_query_producenot="notProduce";//查询未发布的数据{生成最终页时}
    
    public final static String map_params_true="1";//map参数-是
    public final static String map_params_false="0";//map参数-否
    
    public final static String map_params_like="%";//map参数-模糊查询标识
    
	

}
