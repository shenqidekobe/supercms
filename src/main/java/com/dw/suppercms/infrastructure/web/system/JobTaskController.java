package com.dw.suppercms.infrastructure.web.system;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.system.JobTaskService;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_STATE;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_TYPE;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.JobTaskLogInfo;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 任务处理控制器
 * */
@RestController
@RequestMapping("/systems")
public class JobTaskController extends BaseController{
	
	@Resource
	private JobTaskService jobTaskService;
	
	/**
	 * 验证任务名称是否唯一
	 * */
	@RequestMapping(value = "/tasks/validateTaskName", method = { RequestMethod.GET })
	public boolean validateTaskName(Long id, String taskName) {
		boolean valid = true;
		if (id == null) {
			valid = jobTaskService.validateTaskName(taskName);
		} else {
			JobTaskInfo task = jobTaskService.findJobTask(id);
			if (!task.getTaskName().equals(taskName)) {
				valid = jobTaskService.validateTaskName(taskName);
			}
		}
		return valid;
	}
	
	/**
	 * 验证时间表达式是否正确
	 * */
	@RequestMapping(value = "/tasks/validateTaskExperess", method = { RequestMethod.GET })
	public boolean validateTaskExperess(Long id, String taskExperess) {
		boolean valid = CronExpression.isValidExpression(taskExperess);
		return valid;
	}


	/**
	 * 获取所有任务信息
	 * */
	@RequestMapping(value = "/tasks", method = { RequestMethod.GET })
	public List<JobTaskInfo> all() {
		return jobTaskService.findAll();
	}

	/**
	 * 任务列表查询
	 * */
	@RequestMapping(value = "/tasks", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(String taskState,String taskType,int draw, int start, int length) {
		TASK_STATE state=StringUtils.isEmpty(taskState)?null:TASK_STATE.valueOf(TASK_STATE.class,taskState);
		TASK_TYPE type=StringUtils.isEmpty(taskType)?null:TASK_TYPE.valueOf(TASK_TYPE.class,taskType);
		SearchResult<JobTaskInfo> data = jobTaskService.findJobTaskList(state, type, start, length);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}
	
	/**
	 * 任务日志列表查询
	 * */
	@RequestMapping(value = "/tasks", method = { RequestMethod.GET }, params = { "tasklogs" })
	public String tasklogs(Long taskId,Date startTime, Date endTime,Pager pager) {
		SearchResult<JobTaskLogInfo> data = jobTaskService.findJobTaskLogList(taskId, startTime, endTime, pager);
		String json=JSONSerializer().deepSerialize(data);
		return json;
	}

	/**
	 * 获取任务信息
	 * */
	@RequestMapping(value = "/tasks/{id}", method = { RequestMethod.GET })
	public JobTaskInfo id(@PathVariable Long id) {
		return jobTaskService.findJobTask(id);
	}

	/**
	 * 创建任务
	 * */
	@RequestMapping(value = "/tasks", method = { RequestMethod.POST })
	@SystemLog(operation="创建任务",operType=OPER_TYPE.create)
	@Description("创建任务")
	@RequiresPermissions({ "app.settings.task.create" })
	public JobTaskInfo create(@RequestBody @Valid JobTaskInfo task) {
		JobTaskInfo newJobTaskInfo = jobTaskService.createJobTask(task);
		requestBodyAsLog(newJobTaskInfo);
		return newJobTaskInfo;
	}

	/**
	 * 编辑任务
	 * */
	@RequestMapping(value = "/tasks/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑任务",operType=OPER_TYPE.save)
	@Description("修改任务")
	@RequiresPermissions({ "app.settings.task.save" })
	public JobTaskInfo save(@RequestBody @Valid JobTaskInfo task, BindingResult br) {
		JobTaskInfo newJobTask=jobTaskService.modifyJobTask(task.getId(), task);
		requestBodyAsLog(newJobTask);
		return newJobTask;
	}
	
	/**
	 * 启用or禁用任务
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/tasks", method = { RequestMethod.POST }, params = { "toggle" })
	@SystemLog(operation="任务",operType=OPER_TYPE.enable)
	@Description("启用任务")
	@RequiresPermissions({ "app.settings.task.toggle" })
	public JobTaskInfo toggle(@RequestBody Map map) {
		Integer id=(Integer) map.get("id");
		String taskState=(String) map.get("taskState");
		TASK_STATE state=StringUtils.isEmpty(taskState)?null:TASK_STATE.valueOf(TASK_STATE.class,taskState);
		JobTaskInfo newJobTask=jobTaskService.modifyJobTaskState(Long.parseLong(id.toString()), state);
		
		requestBodyAsLog(map);
		return newJobTask;
	}
	
	/**
	 * 触发任务，立即执行
	 * */
	@RequestMapping(value = "/tasks", method = { RequestMethod.POST }, params = { "tigger" })
	@SystemLog(operation="触发任务",operType=OPER_TYPE.trigger)
	@Description("触发任务")
	@RequiresPermissions({ "app.settings.task.trigger" })
	public void doTrigger(Long id){
		this.jobTaskService.triggerJobTask(id);
	}

	/**
	 * 删除任务
	 * */
	@RequestMapping(value = "/tasks/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除任务",operType=OPER_TYPE.remove)
	@Description("删除任务")
	@RequiresPermissions({ "app.settings.task.remove" })
	public void remove(@PathVariable Long id) {
		jobTaskService.removeJobTask(id);
	}

}
