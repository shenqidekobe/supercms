package com.dw.suppercms.application.crontab.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 重建数据的索引
 * */
public class RebuildDataIndexJobTask extends AbstractQuartzJob{
	

	@Override
	protected void runTimingTask(JobExecutionContext context)
			throws JobExecutionException {
		indexSearchService.pushAll();
	}

}
