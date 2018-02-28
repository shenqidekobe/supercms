package com.dw.suppercms.application.crontab;

import java.util.List;

import com.dw.suppercms.domain.system.JobTaskInfo;


/**
 * 任务中心服务接口
 * */
public interface CrontabTaskService {
	
	
	/**
	 * 加载一个任务
	 * @param jobTask
	 * */
	void loadJobTask(JobTaskInfo jobTask);
	
	/**
	 * 加载已经启动的任务列表
	 * */
	void loadEnableJobTaskList(List<JobTaskInfo> jobLists);
	
	/**
	 * 重新设置任务的执行时间
	 * @param jobTask
	 * */
	void resumeJobTaskTime(JobTaskInfo jobTask);
	
	/**
	 * 卸载一个任务
	 * @param jobTask
	 * */
	void unloadJobTask(JobTaskInfo jobTask);
	
	/**
	 * 触发一个任务
	 * @param jobTask
	 * */
	void triggerJobTask(JobTaskInfo jobTask);
	
	/**
	 * 暂停任务
	 * @param jobTask
	 * */
	void pauseJobTask(JobTaskInfo jobTask);
	
	/**
	 * 恢复任务
	 * @param jobTask
	 * */
	void resumeJobTask(JobTaskInfo jobTask);

}
