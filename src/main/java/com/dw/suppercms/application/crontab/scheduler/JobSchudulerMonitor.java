package com.dw.suppercms.application.crontab.scheduler;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dw.suppercms.infrastructure.common.ConstantConfig;

/**
 * 任务调度器运行监控
 * @author kobe
 * @version 1.0
 * */
@Service
public class JobSchudulerMonitor extends Thread{
	
	private Logger logger=LoggerFactory.getLogger(JobSchudulerMonitor.class);
	
	private static volatile boolean is_stop=false;
	
	private static long sleep_times=1000*60*60;//默认一个小时提示一次调度器的任务数据
	
	private static volatile boolean isStart=false;
	
	@Resource(name="schedulerFactoryBean")
	private Scheduler scheduler;
	
	@Resource
	private ConstantConfig constantConfig;
	
	@PostConstruct
	public void onStart(){
		if(!isStart){
			logger.info("任务调度线程监控开始启动...");
			sleep_times=Long.parseLong(constantConfig.getJobTaskSleepTimes());
			this.setName("job-scheduler-monitor");
			this.setDaemon(true);
			this.start();
			isStart=true;
		}
	}
	
	@PreDestroy
	public void onDestory(){
		isStart=false;
		this.interrupt();
	}
	
	
	@Override
	public void run(){
		while(!is_stop){
			try {
				Thread.sleep(sleep_times);
			} catch (InterruptedException e) {
			}
			try {
				logger.info("当前任务调度器名称 ："+scheduler.getSchedulerName());
				logger.info("当前任务调度是否停止： "+scheduler.isShutdown());
				logger.info("**********所有的JobTask如下：***********");
				getJobAll();
				logger.info("********当前正在运行的JobTask如下：********");
				isRunJob();
				logger.info("************************************");
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void isRunJob() throws SchedulerException {
		List<JobExecutionContext> executingJobs = scheduler
				.getCurrentlyExecutingJobs();
		for (JobExecutionContext executingJob : executingJobs) {
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			logger.info("runing job is " + jobKey);
		}
	}
	private void getJobAll() throws SchedulerException {
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler
					.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				logger.info("trigger job is " + trigger.getJobKey());
			}
		}
	}

	
	
}
