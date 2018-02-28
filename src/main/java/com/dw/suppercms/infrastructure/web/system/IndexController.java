package com.dw.suppercms.infrastructure.web.system;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.framework.transform.TextValue;
import com.dw.suppercms.application.crontab.scheduler.JobSchedulerCentre;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskInfo.TASK_TYPE;
import com.dw.suppercms.infrastructure.web.BaseController;

/**
 * 数据索引控制器
 * */
@RestController
@RequestMapping("/systems")
public class IndexController extends BaseController {
	
	@Autowired
	private JobSchedulerCentre jobSchedulerCentre;
	
	private static final String intervalFlag="execute_index_interval";
	
	private static final Integer execute_index_interval=5;
	/**
	 * 重建数据索引
	 * */
	@RequestMapping(value="indexs",method=RequestMethod.POST,params={"execute"})
	public TextValue execute(HttpServletRequest request){
		if(!beforeAfterExecuteTimeCompare((Date)request.getSession().getAttribute(intervalFlag))){
			logger.info("频繁执行索引重建任务，请稍后再试，间隔时间为"+execute_index_interval+"分钟.");
			return new TextValue("wait", "您的操作过于频繁，请稍后再试!");
		}
		JobTaskInfo jobTask=JobTaskInfo.newOf();
		try {
			jobTask.setTaskType(TASK_TYPE.RebuildDataIndex);
			jobTask.setTaskExperess(null);
			jobTask.setTaskName("rebuild-data-index-001");
			jobTask.setTaskTitle("重建数据索引任务");
			jobTask.setTargets(null);
			jobTask=JobTaskInfo.newOf(jobTask);
			jobSchedulerCentre.addJob(jobTask);
			request.getSession().setAttribute(intervalFlag, new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return new TextValue("error", e.toString()+":"+e.getMessage());
		}finally{
			try {
				jobSchedulerCentre.deleteJob(jobTask);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		return new TextValue("success", "后台正在执行重建数据索引任务。");
	}
	
	/**
	 * 前后执行时间的比较
	 * */
	private boolean beforeAfterExecuteTimeCompare(Date beforeTime){
		if(beforeTime==null)
			return true;
		Calendar calendar=Calendar.getInstance(); 
		calendar.add(Calendar.MINUTE, -execute_index_interval);//当前时间减去设置的间隔时间，如果还在前次执行时间之前则为true
		if(beforeTime.before(calendar.getTime())){
			return true;
		}
		return false;
	}

}
