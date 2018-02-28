package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.data.ColumnDataService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.data.DataRecordTransformConvetor;
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 数据记录指令
 * 
 */
@SuppressWarnings("rawtypes") 
@Service
public class RecordDirective extends BaseDirective {
	
	//指令参数说明
	private static final String params_id="id";  //栏目ID
	private static final String params_excludeId="excludeId";  //排除的数据ID
	private static final String params_paging="paging";//是否需要分页、默认否
	private static final String params_tree="tree";//是否需要栏目的多级关系、默认否
	private static final String params_pageSize="pageSize";//分页的每页大小
	private static final String params_sort="sort";//排序字段
	private static final String params_dir="dir"; //排序方式、升序or降序
	private static final String params_pageth="pageth";//当前页
	private static final String params_pages="pages";//需要生成几页
	
	//查询map的请求参数排除当前指定已经明确定义的参数
	private static String[] excludeParamsKey={params_id,VariableDefine.map_params_query_exclude_key,params_paging,VariableDefine.variable_cid,params_tree,params_pageSize,params_sort,params_dir,params_pageth,params_pages};
	
	
	@Resource
	private ProduceFileService produceFileService;
	
	@Resource
	private ColumnDataService columnDataService;
	
	// 执行指令
	@Override
	public void execute(Environment env, Map params, TemplateModel[] models, TemplateDirectiveBody body) throws TemplateException,IOException {
		try {
			List dataList= parseEnviroment(env,params);
			env.setVariable(VariableDefine.variable_list, env.getConfiguration().getObjectWrapper().wrap(dataList));
			if (body != null) {
				body.render(env.getOut());
			}
		} catch (Exception e) {
			String desc=String.format("数据指令解析错误,params=%s", params);
			saveDirectiveParseErrorLog(desc, e.toString()+":"+e.getMessage());
		}
	}
	
	/**
	 * 解析栏目ID的参数、获取当前栏目ID集合中的第一个栏目、作为其数据源的数据对象查询入口
	 * */
	private List parseEnviroment(Environment env,Map params)throws TemplateException{
		List result = new ArrayList();
		// 要提取数据的栏目ID
		String id = params.get(params_id).toString();
		String[] ids=StringUtils.split(id,",");
		Column column = produceFileService.findColumnById(Long.valueOf(ids[0]));
		List<Long> columnIds = null;
		if (column != null) {
			if (column.getDatasource() != null) {
				columnIds=new ArrayList<Long>();
				for (String cid : ids) {
					columnIds.add(Long.valueOf(cid));
				}
				result=getDataList(env,params,columnIds,column);
			}
		}
		return result;
	}
	
	/**
	 * 封装查询的数据,解析本地的数据模块数据
	 * */
	private List getDataList(Environment env,Map params,List<Long> columnIds,Column column)throws TemplateException{
		String excludeId=params.get(params_excludeId)==null?null:params.get(params_excludeId).toString();
		// 是否分页
		boolean paging = params.get(params_paging) == null ? false : true;
		boolean tree = params.get(params_tree) == null ? false : true;
		// 分页大小
		Integer pageSize = params.get(params_pageSize) == null ? DEFAULT_PAGE_SIZE : Integer.valueOf(params.get(params_pageSize).toString());
		// 排序字段
		String sort = params.get(params_sort) == null ? "" : params.get(params_sort).toString();
		// 排序方向
		String dir = params.get(params_dir) == null ? "" : params.get(params_dir).toString();
		// 第几页
		Integer pageth = 1;
		if (paging) {
			pageth = env.getDataModel().get(params_pageth) == null ? 1 : Integer.valueOf(env.getDataModel().get(params_pageth).toString());
		}
		Integer pages = params.get(params_pages) == null ? 200 : Integer.valueOf(params.get(params_pages).toString());
		Date startTime=null,endTime=null;
		
		// 当前模板处理关联的数据模型中的栏目
		Column processColumn =null;
		String cid=VariableDefine.variable_cid;
		String history=VariableDefine.variable_history;
		if (env.getDataModel().get(cid) != null) {
			processColumn= produceFileService.findColumnById(Long.valueOf(env.getDataModel().get(cid).toString()));
		}
		//需要生成历史列表列表文件的参数
		if(env.getDataModel().get(history)!=null){
			pageSize = env.getDataModel().get(VariableDefine.variable_pagesize) == null ? 1 : Integer.valueOf(env.getDataModel().get(VariableDefine.variable_pagesize).toString());
			try {
				startTime = env.getDataModel().get(VariableDefine.variable_start) == null ? null : DateUtils.parseDate(env.getDataModel().get(VariableDefine.variable_start).toString(), "yyyy-MM-dd HH:mm:ss");
				endTime = env.getDataModel().get(VariableDefine.variable_end) == null ? null : DateUtils.parseDate(env.getDataModel().get(VariableDefine.variable_end).toString(), "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			pages=100;
		}
		
		Map<String,Object> queryMap=new HashMap<>();
		queryMap.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_produced);//列表页面只获取已生成的数据
		for(Object field:params.keySet()){
			if(!ArrayUtils.contains(excludeParamsKey, field.toString())){
				queryMap.put(field.toString(), params.get(field));
			}
		}
		if(excludeId!=null&&!",".equals(excludeId)){
			if(excludeId.endsWith(",")){
				excludeId=StringUtils.chop(excludeId);//如果是以，结尾就去掉最后一位
			}
			queryMap.put(VariableDefine.map_params_query_exclude_key, excludeId);
		}
				
		List<DynaBean> list = new ArrayList<DynaBean>();
		
		Pager pager = new Pager();
		pager.setPageSize(pageSize);
		pager.setPageth(pageth);
		pager.setSort(sort);
		pager.setDir(dir);
		
		List<Long> allColumnIds = !tree ? columnIds : produceFileService.queryColumnTreeByParentIds(columnIds);
		SearchResult<DataRecord> searchResult=this.produceFileService.findDataRecordListUseDirective(queryMap, allColumnIds, startTime, endTime, pager);
		long totalCount=searchResult.getTotalCount();
		pager.calcPageCount(totalCount);
		List<DataRecord> dataList=searchResult.getResult();
		log.debug("此次总共需要生成："+totalCount+"条数据."+"当前页dataList size = "+dataList.size()+"需要生成的栏目ID："+allColumnIds+",分页参数：paging:"+paging+",tree:"+tree+",pageSize:"+pageSize+",pageth:"+pageth+",pages:"+pages+",sort:"+sort+",dir:"+dir);
		//验证是否需要分页
		if (paging) {
			if (pages < pager.getPageCount()) {
				pager.configPages(pages);
			}
			String customAttrKey=(processColumn != null ? processColumn.getId().toString() : "") + VariableDefine.pading_pagethNum;
			env.getTemplate().setCustomAttribute(customAttrKey, pager.getPageCount());
		}
		
		
		for(DataRecord dataRecord:dataList){
			//多栏目查询需根据数据源ID和数据ID确定当前是哪个栏目
			if (columnIds.size() >1) {
				column = columnDataService.queryColumnByDataId(allColumnIds, Long.parseLong(dataRecord.getId()),dataRecord.getDataSourceId());
			}
			DynaBean dynaBean=DataRecordTransformConvetor.dataRecordTransformDynaBean(dataRecord,column);
			list.add(dynaBean);
		}
		
		List<String> pagethsHrefList = new ArrayList<String>();
		List<Integer> pagethNumList = new ArrayList<Integer>();
		List<Integer> pageths = pager.getPageths();
		
		if (processColumn != null) {
			for (Integer currentPageth : pageths) {
				pagethNumList.add(currentPageth);
				pagethsHrefList.add(processColumn.getFileWebpath(currentPageth));
			}
			env.setVariable(VariableDefine.variable_coid, env.getConfiguration().getObjectWrapper().wrap(processColumn.getId()));
			env.setVariable(VariableDefine.pading_first, env.getConfiguration().getObjectWrapper().wrap(processColumn.getFileWebpath(1)));
			env.setVariable(VariableDefine.pading_prev, env.getConfiguration().getObjectWrapper().wrap(processColumn.getFileWebpath(pager.getPrev())));
			env.setVariable(VariableDefine.pading_next, env.getConfiguration().getObjectWrapper().wrap(processColumn.getFileWebpath(pager.getNext())));
			env.setVariable(VariableDefine.pading_last, env.getConfiguration().getObjectWrapper().wrap(processColumn.getFileWebpath(pager.getPageCount())));
		}
		env.setVariable(VariableDefine.pading_pagethNum, env.getConfiguration().getObjectWrapper().wrap(pager.getPageCount()));
		env.setVariable(VariableDefine.pading_rowNum, env.getConfiguration().getObjectWrapper().wrap(pager.getRowCount()));
		env.setVariable(VariableDefine.pading_pageth, env.getConfiguration().getObjectWrapper().wrap(pageth));
		env.setVariable(VariableDefine.pading_pagethsHrefList, env.getConfiguration().getObjectWrapper().wrap(pagethsHrefList));
		env.setVariable(VariableDefine.pading_pagethNumList, env.getConfiguration().getObjectWrapper().wrap(pagethNumList));
		env.setVariable(VariableDefine.pading_pageCount, env.getConfiguration().getObjectWrapper().wrap(pager.getPageCount()));
		
		return list;
	}


	private static final Integer DEFAULT_PAGE_SIZE = 5;
	
}
