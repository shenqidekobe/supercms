package com.dw.suppercms.application.crontab.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 资源中心任务
 * @author kobe
 * */
public class ResourceJobTask extends AbstractQuartzJob{

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		logger.debug("内容中心任务待实现...");
	}

	
}
