package com.dw.suppercms.application.crontab.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 刷新栏目任务,先执行内容页任务，再执行列表页任务
 * @author kobe
 * */
public class RefreshColumnJobTask extends AbstractQuartzJob{

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		List<Column> columns=null;
		if(JobTaskInfo.ALL_IDENTIFY.equals(getParams())){
			columns=produceFileService.findAllColumns();
		}else{
			columns=produceFileService.findColumnByIds(getParams());
		}
		if(columns.isEmpty())return;
		Map<Column,List<DataRecord>> dataMap=new HashMap<Column, List<DataRecord>>();
		//刷新栏目内容页，获取该栏目的所有数据
		Pager pager=new Pager();
		pager.setPageSize(Integer.MAX_VALUE);
		Map<String,Object> params=new HashMap<String, Object>();
		params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_produceall);
		for(Column column:columns){
			SearchResult<DataRecord> sr=this.produceFileService.findDataRecordListUseDirective(params, Arrays.asList(column.getId()), null, null, pager);
			dataMap.put(column, sr.getResult());
		}
		MakeFileResult result=makeFileService.makeContentAndListFileTask(columns, dataMap);
		
		stdTaskResultProcress(result);
	}


}
