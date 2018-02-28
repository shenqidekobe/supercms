package com.dw.suppercms.domain.system;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 系统任务对象
 * @author kobe
 * */
@Data(staticConstructor = "newOf")
@Entity
@EqualsAndHashCode(callSuper = false)
public class JobTaskInfo extends IdentifiedEntity{

	private static final long serialVersionUID = 5959631610070129689L;
	
	public static final String ALL_IDENTIFY="all";//表示任务目标为：全部
	
	//开启、禁用
	public enum TASK_STATE{
		Enable,Disable
	}
	
	//{站点更新、栏目更新、栏目列表更新、栏目内容更新、历史列表页更新、自定义页更新、采集任务、内容中心任务、重建数据的索引}
	public enum TASK_TYPE{
		RefreshSite,RefreshColumn,RefreshColumnList,RefreshColumnContent,RefreshColumnHistoryList,
		RefreshCustom,CollectTask,ResourceTask,
		RebuildDataIndex
	}
	
	private String taskTitle;//任务标题{中文标题}
	private String taskName;//任务名称{任务代码标题唯一}
	private String taskGroup;//任务分组
	private String taskExperess;//任务执行时间表达式
	@Enumerated(EnumType.STRING)
	private TASK_STATE taskState; //任务状态
	@Enumerated(EnumType.STRING)
	private TASK_TYPE taskType;//任务类型
	private String taskImplClass;//任务实现类路径
	@Column(length=2000)
	private String taskTarget;//任务目标
	@Column(length=2000)
	private String taskParams;//任务参数
	
	@JsonIgnore
	@OneToMany(mappedBy = "jobTask")
	private Set<JobTaskLogInfo> taskLogs;
	
	@Transient
	private String[] targets;

	public static JobTaskInfo newOf(JobTaskInfo jobTask) {
		JobTaskInfo newTask = JobTaskInfo.newOf();
		newTask.setTaskTitle(jobTask.getTaskTitle());
		newTask.setTaskName(jobTask.getTaskName());
		newTask.setTaskExperess(jobTask.getTaskExperess());
		newTask.setTaskType(jobTask.getTaskType());
		newTask.setTaskState(jobTask.getTaskState());
		newTask.setTargets(jobTask.getTargets());
		newTask.setTaskParams(jobTask.getTaskParams());
		if(jobTask.getTargets()!=null){
			String taskTarget=StringUtils.join(jobTask.getTargets(), ",");
			if(ArrayUtils.contains(jobTask.getTargets(), ALL_IDENTIFY)){
				taskTarget=ALL_IDENTIFY;
			}
			newTask.setTaskTarget(taskTarget);
		}
		String taskGroup="";
		String taskImplClass="";
		switch (jobTask.getTaskType()) {
		case RefreshSite:
			taskGroup="group1";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshSiteJobTask";
			break;
		case RefreshColumn:
			taskGroup="group2";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshColumnJobTask";
			break;
		case RefreshColumnList:
			taskGroup="group3";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshColumnListJobTask";
			break;
		case RefreshColumnContent:
			taskGroup="group4";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshColumnContentJobTask";
			break;
		case RefreshCustom:
			taskGroup="group5";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshCustomJobTask";
			break;
		case CollectTask:
			taskGroup="group6";
			taskImplClass="com.dw.suppercms.application.crontab.job.CollectJobTask";
			break;
		case ResourceTask:
			taskGroup="group7";
			taskImplClass="com.dw.suppercms.application.crontab.job.ResourceJobTask";
			break;
		case RefreshColumnHistoryList:
			taskGroup="group8";
			taskImplClass="com.dw.suppercms.application.crontab.job.RefreshColumnHistoryListJobTask";
			break;
		case RebuildDataIndex:
			taskGroup="group9";
			taskImplClass="com.dw.suppercms.application.crontab.job.RebuildDataIndexJobTask";
			break;
		default:
			break;
		}
		newTask.setTaskGroup(taskGroup);
		newTask.setTaskImplClass(taskImplClass);
		return newTask;
	}
	
	public static String typeToChar(TASK_TYPE taskType){
		String character="";
		switch (taskType) {
		case RefreshSite:
			character="站点刷新任务";
			break;
		case RefreshColumn:
			character="栏目刷新任务";
			break;
		case RefreshColumnList:
			character="栏目列表刷新任务";
			break;
		case RefreshColumnContent:
			character="栏目内容刷新任务";
			break;
		case RefreshColumnHistoryList:
			character="栏目历史列表刷新任务";
			break;
		case RefreshCustom:
			character="自定义页刷新任务";
			break;
		case CollectTask:
			character="采集任务";
			break;
		case ResourceTask:
			character="内容中心任务";
			break;
		case RebuildDataIndex:
			character="重建数据索引任务";
			break;
		default:
			break;
		}
		return character;
	}
}
