package com.dw.suppercms.application.crontab.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.produce.MakeFileResult;

/**
 * 刷新自定义页任务
 * @author kobe
 * */
public class RefreshCustomJobTask extends AbstractQuartzJob{

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		List<Custom> customs=null;
		if(JobTaskInfo.ALL_IDENTIFY.equals(getParams())){
			customs=produceFileService.findAllCustoms();
		}else{
			customs=produceFileService.findCustomByIds(getParams());
		}
		
		if(customs.isEmpty())return;
		MakeFileResult result=makeFileService.makeCustomFileTask(customs);
		stdTaskResultProcress(result);
		
		//执行成功的自定义页步到发布目录
		for(Custom custom:result.getSuccessCustoms()){
			String src=custom.getFileDiskpath();
			String desc=custom.getPubFileDiskpath();
			syncMakeFile(src, desc, true);
		}
	}

}
