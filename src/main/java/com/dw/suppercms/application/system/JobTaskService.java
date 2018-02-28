package com.dw.suppercms.application.system;

import java.util.Date;
import java.util.List;

import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_STATE;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_TYPE;
import com.dw.suppercms.domain.system.JobTaskLogInfo;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 系统任务服务接口
 * @author kobe
 * */
public interface JobTaskService {
	
	
	/**
	 * 创建一个任务
	 * @param task
	 * @return JobTaskInfo
	 * */
	JobTaskInfo createJobTask(JobTaskInfo task);
	
	/**
	 * 验证任务名称是否存在
	 * @param taskName
	 * @return boolean
	 * */
	boolean validateTaskName(String taskName);
	
	/**
	 * 更新任务
	 * @param id
	 * @param task
	 * @return JobTaskInfo
	 * */
	JobTaskInfo modifyJobTask(Long taskId,JobTaskInfo task);
	
	/**
	 * 更新任务的状态
	 * @param id
	 * @param taskState
	 * @return JobTaskInfo
	 * */
	JobTaskInfo modifyJobTaskState(Long taskId,TASK_STATE taskState);
	
	/**
	 * 删除一个任务
	 * @param taskId
	 * */
	void removeJobTask(Long taskId);
	
	/**
	 * 触发任务
	 * @param taskId
	 * */
	void triggerJobTask(Long taskId);
	
	/**
	 * 获取一个任务信息
	 * @param id
	 * @return JobTask
	 * */
	JobTaskInfo findJobTask(Long taskId);
	
	/**
	 * 加载所有的任务
	 * */
	List<JobTaskInfo> findAll();
	
	/**
	 * 按任务状态检索任务列表
	 * */
	List<JobTaskInfo> findJobTaskListByState(TASK_STATE taskState);
	
	/**
	 * 按任务状态、任务类型、进行分页检索任务列表
	 * @param taskState
	 * @param taskType
	 * @param startIndex
	 * @param maxResult
	 * */
	SearchResult<JobTaskInfo> findJobTaskList(TASK_STATE taskState,TASK_TYPE taskType,int startIndex,int maxResult);
	
	
	
	
	/**
	 * 保存任务日志
	 * @param taskLog
	 * */
	void creareJobTaskLog(JobTaskLogInfo taskLog);
	
	/**
	 * 查询任务运行日志列表
	 * @param taskId
	 * @param startTime
	 * @param endTime
	 * */
	SearchResult<JobTaskLogInfo> findJobTaskLogList(Long taskId,Date startTime,Date endTime,Pager pager);
	
}
