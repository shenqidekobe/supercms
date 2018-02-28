package com.dw.suppercms.application.crontab.impl;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.crontab.CrontabTaskService;
import com.dw.suppercms.application.crontab.scheduler.JobSchedulerCentre;
import com.dw.suppercms.domain.system.JobTaskInfo;

@Service
public class CrontabTaskServiceImpl implements CrontabTaskService{
	
	private static Logger logger=LoggerFactory.getLogger(CrontabTaskServiceImpl.class);
	
	@Resource
	private JobSchedulerCentre jobSchedulerCentre;
	
	@Override
	public void loadJobTask(JobTaskInfo jobTask) {
		try {
			jobSchedulerCentre.addJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("添加任务失败："+e);
			e.printStackTrace();
		}
	}

	@Override
	public void loadEnableJobTaskList(List<JobTaskInfo> jobTaskList) {
		try {
			for (JobTaskInfo jobTask : jobTaskList) {
				jobSchedulerCentre.addJob(jobTask);
			}
		} catch (SchedulerException e) {
			logger.error("批量添加任务失败："+e);
			e.printStackTrace();
		}
	}

	@Override
	public void unloadJobTask(JobTaskInfo jobTask) {
		try {
			jobSchedulerCentre.deleteTriggerAndJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("卸载任务失败："+e);
			e.printStackTrace();
		}
	}

	@Override
	public void triggerJobTask(JobTaskInfo jobTask) {
		try {
			jobSchedulerCentre.triggerJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("触发任务失败："+e);
			e.printStackTrace();
		}
	}

	@Override
	public void resumeJobTaskTime(JobTaskInfo jobTask) {
		try {
			jobSchedulerCentre.rescheduleJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("修改任务时间表达式失败："+e);
			e.printStackTrace();
		}
	}
	
	@Override
    public void pauseJobTask(JobTaskInfo jobTask){
		try {
			jobSchedulerCentre.pauseJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("暂停任务失败："+e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void resumeJobTask(JobTaskInfo jobTask){
		try {
			jobSchedulerCentre.resumeJob(jobTask);
		} catch (SchedulerException e) {
			logger.error("恢复任务失败："+e);
			e.printStackTrace();
		}
	}

}
