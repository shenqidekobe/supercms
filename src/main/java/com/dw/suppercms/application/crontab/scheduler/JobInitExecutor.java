package com.dw.suppercms.application.crontab.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.crontab.impl.CrontabTaskServiceImpl;
import com.dw.suppercms.application.crontab.job.RefreshColumnHistoryListJobTask;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.application.system.JobTaskService;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_STATE;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_TYPE;

/**
 * 应用初始化启动任务线程
 * @author kobe
 * */
@Service
public class JobInitExecutor extends Thread{
	
	private static Logger logger=LoggerFactory.getLogger(CrontabTaskServiceImpl.class);
	
	@Resource
	private JobTaskService jobTaskService;
	
	@Resource
	private JobSchedulerCentre jobSchedulerCentre;
	
	@Resource
	private ProduceFileService produceFileService;
	
	@PostConstruct
	public void onstart(){
		logger.info("job调度初始化线程开始启动...");
		this.setName("job-schedule-init");
		this.start();
		try {
			jobSchedulerCentre.startJobs();
		} catch (SchedulerException e) {
			logger.error("启动job调度器失败："+e);
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void ondestory(){
		try {
			jobSchedulerCentre.shutdownJobs();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}finally{
			this.interrupt();
		}
	}

	@Override
	public void run() {
		try {
			//加载系统已经开启的任务
			List<JobTaskInfo> jobTaskList=this.jobTaskService.findJobTaskListByState(TASK_STATE.Enable);
			for (JobTaskInfo jobTask : jobTaskList) {
				jobSchedulerCentre.addJob(jobTask);
			}
			//加载生成栏目历史的列表任务
			JobTaskInfo jobTask=JobTaskInfo.newOf();
			jobTask.setTaskType(TASK_TYPE.RefreshColumnHistoryList);
			jobTask.setTaskExperess(RefreshColumnHistoryListJobTask.cronExpression_month);
			jobTask.setTaskName("built-in-makeHistoryListFile-001");
			jobTask.setTaskTitle("生成栏目的历史列表任务");
			jobTask.setTargets(null);
			jobSchedulerCentre.addJob(JobTaskInfo.newOf(jobTask));
		} catch (SchedulerException e) {
			logger.error("批量添加任务失败："+e);
			e.printStackTrace();
		}
	}

}
