package com.dw.suppercms.application.crontab.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.produce.MakeFileResult;

/**
 * 刷新栏目列表任务
 * @author kobe
 * */
public class RefreshColumnListJobTask extends AbstractQuartzJob{

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		List<Column> columns=null;
		if(JobTaskInfo.ALL_IDENTIFY.equals(getParams())){
			columns=produceFileService.findAllColumns();
		}else{
			columns=produceFileService.findColumnByIds(getParams());
		}
		if(columns.isEmpty())return;
		MakeFileResult result=makeFileService.makeListFileTask(columns,null);
		stdTaskResultProcress(result);
		
		//执行成功的栏目列表同步到发布目录,只同步当前栏目目录的列表页
		for(Column column:result.getSuccessColumns()){
			String src=column.getDirDiskpath()+"/*."+column.getExtensionName();
			String desc=column.getPubDiskpath();
			syncMakeFile(src, desc, true);
		}
	}

	
}
