package com.dw.suppercms.application.crontab.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 采集任务
 * @author kobe
 * */
public class CollectJobTask extends AbstractQuartzJob{

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		logger.debug("采集任务待实现...");
	}

	
}
