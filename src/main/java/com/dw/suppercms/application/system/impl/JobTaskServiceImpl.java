package com.dw.suppercms.application.system.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.crontab.CrontabTaskService;
import com.dw.suppercms.application.system.JobTaskService;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_STATE;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_TYPE;
import com.dw.suppercms.domain.system.JobTaskLogInfo;
import com.dw.suppercms.domain.system.JobTaskLogRepository;
import com.dw.suppercms.domain.system.JobTaskRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 任务服务接口实现
 * @author kobe
 * */
@ApplicationService
public class JobTaskServiceImpl implements JobTaskService{
	
	@Resource
	private JobTaskRepository jobTaskRepository;
	
	@Resource
	private JobTaskLogRepository jobTaskLogRepository;
	
	@Resource
	private CrontabTaskService crontabTaskService;
	

	@Override
	public JobTaskInfo createJobTask(JobTaskInfo task) {
		assertNotNull(task);
		JobTaskInfo newTask=JobTaskInfo.newOf(task);
		this.jobTaskRepository.save(newTask);
		if(newTask.getTaskState().equals(TASK_STATE.Enable)){
			crontabTaskService.loadJobTask(newTask);
		}
		return task;
	}

	@Override
	public boolean validateTaskName(String taskName) {
		assertNotNull(taskName);
		return this.jobTaskRepository.search(new Search().addFilterEqual("taskName", taskName)).size()==0?true:false;
	}

	@Override
	public JobTaskInfo modifyJobTask(Long taskId, JobTaskInfo task) {
		assertNotNull(task);
		JobTaskInfo newTask=JobTaskInfo.newOf(task);
		JobTaskInfo obj=this.findJobTask(taskId);
		BeanUtils.copyProperties(newTask, obj);
		this.jobTaskRepository.save(obj);
		//当任务类型以及任务目标改变、则需要重新加载任务
		if(!obj.getTaskType().equals(task.getTaskType())||!obj.getTaskTarget().equals(task.getTaskTarget())){
			crontabTaskService.unloadJobTask(obj);
			if(task.getTaskState().equals(TASK_STATE.Enable)){
				crontabTaskService.loadJobTask(obj);
			}
		}else{
			//当修改了时间表达式的时候需要重新更新任务,并且属于启动状态
			if(!obj.getTaskExperess().equals(task.getTaskExperess())
					&&task.getTaskState().equals(TASK_STATE.Enable)){
				crontabTaskService.resumeJobTaskTime(obj);
			}
		}
		return obj;
	}

	@Override
	public JobTaskInfo modifyJobTaskState(Long taskId, TASK_STATE taskState) {
		assertNotNull(taskState);
		JobTaskInfo task=this.findJobTask(taskId);
		task.setTaskState(taskState);
		if(taskState.equals(TASK_STATE.Enable)){
			crontabTaskService.resumeJobTask(task);
		}else{
			crontabTaskService.pauseJobTask(task);
		}
		
		this.jobTaskRepository.save(task);
		return task;
	}

	@Override
	public void removeJobTask(Long taskId) {
		JobTaskInfo jobTask=this.findJobTask(taskId);
		crontabTaskService.unloadJobTask(jobTask);
		this.jobTaskRepository.remove(jobTask);
	}
	
	@Override
	public void triggerJobTask(Long taskId){
		JobTaskInfo jobTask=this.findJobTask(taskId);
		this.crontabTaskService.triggerJobTask(jobTask);
	}

	@Override
	public JobTaskInfo findJobTask(Long taskId) {
		assertNotNull(taskId);
		JobTaskInfo task= this.jobTaskRepository.find(taskId);
		String[] targets=StringUtils.split(task.getTaskTarget(), ",");
		task.setTargets(targets);
		return task;
	}

	@Override
	public List<JobTaskInfo> findAll() {
		return this.jobTaskRepository.findAll();
	}
	
	@Override
	public List<JobTaskInfo> findJobTaskListByState(TASK_STATE taskState){
		assertNotNull(taskState);
		return this.jobTaskRepository.search(new Search().addFilterEqual("taskState", taskState));
	}

	@Override
	public SearchResult<JobTaskInfo> findJobTaskList(TASK_STATE taskState,
			TASK_TYPE taskType, int startIndex, int maxResult) {
		Search search = new Search(JobTaskInfo.class);
		if (taskState!=null) {
			search.addFilterEqual("taskState", taskState);
		}
		if (taskType != null) {
			search.addFilterEqual("taskType", taskType);
		}
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResult);
		search.setSorts(Lists.newArrayList(new Sort("createTime", true)));

		return jobTaskRepository.searchAndCount(search);
	}

	@Override
	public void creareJobTaskLog(JobTaskLogInfo taskLog) {
		assertNotNull(taskLog);
		this.jobTaskLogRepository.save(taskLog);
	}
	
	@Override
	public SearchResult<JobTaskLogInfo> findJobTaskLogList(Long taskId,Date startTime,Date endTime,Pager pager){
		assertNotNull(taskId);
		assertNotNull(pager);
		Search search = new Search(JobTaskLogInfo.class);
		search.addFilterEqual("taskId", taskId);
		if (startTime!=null) {
			search.addFilterGreaterThan("exeTime", startTime);
		}
		if (endTime != null) {
			search.addFilterLessThan("exeTime", endTime);
		}
		search.setFirstResult(pager.getStartIndex());
		search.setMaxResults(pager.getPageSize());
		search.setSorts(Lists.newArrayList(new Sort("exeTime", true)));

		return jobTaskLogRepository.searchAndCount(search);
	}

}
