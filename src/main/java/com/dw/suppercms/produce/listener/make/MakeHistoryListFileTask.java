package com.dw.suppercms.produce.listener.make;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.data.DataRecordTransformConvetor;
import com.dw.suppercms.produce.execption.MakeHistoryListExecption;
import com.dw.suppercms.produce.listener.MakeTemplateFileProcessorListener;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.MakeFileCommon;
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * 生成历史文件列表任务
 * 主要针对于栏目的历史数据按月生成文件(每月的数据一个文件),已经生成过最终页的数据都生成在历史文件中
 * 规则：每个月只生成当前月的时候，而每个月的一号生成之前一个月的数据
 * */
@Service
public class MakeHistoryListFileTask extends MakeTemplateFileProcessorListener {
	
	@Resource
	private MakeFileCommon makeFileCommon;
	
	@Resource
	private ProduceFileService produceFileService;

	@Override
	protected void makeFile(MakeFileTask task) {
		try {
			Column column=task.getColumn();
			Integer preCount=task.getPreCount();
			//根据生成历史列表的方式来计算时间
			Date produceDate=new Date();
			Date startTime=null,endTime=null;
			if(preCount>0){
				produceDate=getCurrentDateOfPreviouMonth(produceDate,preCount);
				//上个月的数据
				startTime=getCurrentDateOfPreviouFirstDay(produceDate);
				endTime=getCurrentDateOfPreviouEndDay(produceDate);
			}else{
				//当前月的数据
				startTime=getCurrentDateOfFirstDay(produceDate);
				endTime=new Date();
			}
			String historyPath=column.getHistoryListFileDiskpath(produceDate,1);
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info(String.format("执行栏目【历史列表页】生成任务 栏目id=%s 栏目名称=%s 生成地址=%s",task.getFileId(), task.getSourceName(),historyPath)+" 生成开始时间："+sdf.format(startTime)+" 至  "+sdf.format(endTime)+"的历史数据。");
			SimpleHash simpleHash = makeFileCommon.bulidBasicSimpleHash();
			//重新设置环境变量
			simpleHash.put(VariableDefine.variable_history, true);
			simpleHash.put(VariableDefine.variable_pagesize, 10);
			simpleHash.put(VariableDefine.variable_start, sdf.format(startTime));
			simpleHash.put(VariableDefine.variable_end, sdf.format(endTime));
			simpleHash.put(VariableDefine.variable_cid, task.getFileId());
			simpleHash.put(VariableDefine.variable_sid, task.getWebId());
			simpleHash.put(VariableDefine.variable_cname, task.getSourceName());
			simpleHash.put(VariableDefine.variable_pid, task.getParentId());
			simpleHash.put(VariableDefine.variable_clink, column.getHistoryListFileWebpath(produceDate,1));
			
			//生成第一页
			File file = makeFileCommon.createFile(historyPath);
			
			Writer output = new StringWriter();
			Template template = makeFileCommon.getConfiguration().getTemplate(VariableDefine.template_prefix_templateinfo + task.getTemplateId());
			template.process(simpleHash, output);
			FileUtils.write(file, output.toString());
			
			//判断是否需要生成多页
			Object pagethNumObj=template.getCustomAttribute(task.getFileId() +VariableDefine.pading_pagethNum);
			if ( pagethNumObj!= null) {
				int pagethNum = Integer.parseInt(pagethNumObj.toString());
				for (int i = 2; i <= pagethNum; i++) {
					simpleHash.put(VariableDefine.pading_pageth, i);
					output = new StringWriter();
					file = makeFileCommon.createFile(column.getHistoryListFileDiskpath(produceDate,i));
					template.process(simpleHash, output);
					FileUtils.write(file, output.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MakeHistoryListExecption(e.toString() + ":" +e.getMessage());
		}

	}

	@Override
	public boolean supportsMakeType(MakeEventType makeEventType) {
		return MakeEventType.HISTORY.equals(makeEventType);
	}
	
	
	/**
	 * 根据时间获取数据列表
	 * */
	protected List<DynaBean> getDataList(Long columnId,Date startTime,Date endTime){
		List<DynaBean> list = new ArrayList<DynaBean>();
		
		Pager pager=new Pager();
		pager.setPageSize(Integer.MAX_VALUE);
		Map<String,Object> params=new HashMap<String, Object>();
		params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_produced);//查询已经发布的数据
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.debug("当前栏目ID："+columnId+" 开始生成开始时间："+sdf.format(startTime)+" 至  "+sdf.format(endTime)+"的历史数据。");
		SearchResult<DataRecord> sr=this.produceFileService.findDataRecordListUseDirective(params, Arrays.asList(columnId), startTime, endTime, pager);
		
		for(DataRecord dataRecord:sr.getResult()){
			Column column = null;
			DynaBean dynaBean=DataRecordTransformConvetor.dataRecordTransformDynaBean(dataRecord,column);
			list.add(dynaBean);
		}
		return list;
	}
	
	/**
	 * 获取上个月
	 * */
	private Date getCurrentDateOfPreviouMonth(Date date,Integer preCount){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -preCount);
		return calendar.getTime();
	}
	
	/**
	 * 获取当前时间的一号0点0分0秒
	 * */
	private Date getCurrentDateOfFirstDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);//设置当前时间的当月一号
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取当前时间的上个月一号0点0分0秒
	 * historyListMethod
	 * */
	private Date getCurrentDateOfPreviouFirstDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);//设置当前时间的上个月一号
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取当前时间的上个月的最后一天23小时59分59秒
	 * */
	private Date getCurrentDateOfPreviouEndDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);//设置当前时间的上个月的最后一天,即先设置为当前月一号，再减一天
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	
}
