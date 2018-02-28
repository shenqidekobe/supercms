package com.dw.suppercms.application.crontab.scheduler;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.system.JobTaskInfo;

/**
 * 任务调度中心
 * @author kobe
 * */
@Service
public class JobSchedulerCentre {
	
	private static Logger logger=LoggerFactory.getLogger(JobSchedulerCentre.class);
	
	@Resource
	private SchedulerFactory stdSchedulerFactory;
	
	@Resource(name="schedulerFactoryBean")
	private Scheduler scheduler;
	
	public static final String JobDataMapKey="scheduleJobDataMap";

	
	/**
	 * 向任务调度器中添加任务
	 * 时间表达式为null表示立即执行
	 * @param jobTask
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addJob(JobTaskInfo jobTask)throws SchedulerException{
		String name=jobTask.getTaskName();
		String group=jobTask.getTaskGroup();
		TriggerKey triggerKey = TriggerKey.triggerKey(name,group);
		Trigger trigger =scheduler.getTrigger(triggerKey);
	    //验证当前任务是否已经存在，如果不存在就创建一个新的任务加入任务调度器
	    if(trigger==null){
	    	Class clazz=null;
			try {
				String implClass=jobTask.getTaskImplClass();
				if(StringUtils.isEmpty(implClass))return;
				clazz = Class.forName(implClass);
			} catch (ClassNotFoundException e) {
				logger.error(jobTask.getTaskImplClass()+"，任务未定义。");
				throw new SchedulerException(e);
			}
			logger.info("job调度器开始创建任务 trigger = "+triggerKey);
			JobDataMap jobDataMap=new JobDataMap();
			jobDataMap.put(JobDataMapKey, jobTask);
	    	JobDetail jobDetail = JobBuilder.newJob(clazz).setJobData(jobDataMap).withIdentity(name,group).build();
	    	if(jobTask.getTaskExperess()==null){
	    		trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow().build();
	    	}else{
	    		CronScheduleBuilder scheduleBuilder=null;
		    	try {
		    		scheduleBuilder = CronScheduleBuilder.cronSchedule(jobTask.getTaskExperess());
				} catch (Exception e) {
					throw new SchedulerException("任务："+name+"的时间表达式不符合规范");
				}
		    	trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
	    	}
	    	
	        scheduler.scheduleJob(jobDetail, trigger);
	    }
	}
	
	/**
	 * 立即运行任务：触发任务
	 * @param jobTask
	 * */
	public void triggerJob(JobTaskInfo jobTask)throws SchedulerException{
		JobKey jobKey = JobKey.jobKey(jobTask.getTaskName(),jobTask.getTaskGroup());
		if (jobKey != null)
			logger.info("job调度器开始触发任务 jobKey = "+jobKey);
			scheduler.triggerJob(jobKey);
	}
	
	/**
	 * 暂停任务
	 * @param jobTask
	 * */
	public void pauseJob(JobTaskInfo jobTask)throws SchedulerException{
		JobKey jobKey = JobKey.jobKey(jobTask.getTaskName(), jobTask.getTaskGroup());
		if (jobKey != null){
			logger.info("job调度器开始暂停任务 jobKey = "+jobKey);
			scheduler.pauseJob(jobKey);
		}
	}
	
	/**
	 * 恢复任务，如果没有该任务就添加该任务
	 * @param jobTask
	 * */
	public void resumeJob(JobTaskInfo jobTask)throws SchedulerException{
		JobKey jobKey = JobKey.jobKey(jobTask.getTaskName(), jobTask.getTaskGroup());
		if (scheduler.getJobDetail(jobKey)!=null){
			logger.info("job调度器开始恢复任务 jobKey = "+jobKey);
			scheduler.resumeJob(jobKey);
		}else{
			addJob(jobTask);
		}
	}
	
	/**
	 * 更新任务的时间表达式
	 * @param jobTask
	 * */
	public void rescheduleJob(JobTaskInfo jobTask)throws SchedulerException{
		TriggerKey triggerKey = TriggerKey.triggerKey(jobTask.getTaskName(),jobTask.getTaskGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		if(trigger!=null){
			logger.info("job调度器开始更新任务的执行时间 triggerKey = "+triggerKey);
			//表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobTask.getTaskExperess());
			//按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			//按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}
	
	/**
	 * 删除任务
	 * @param jobTask
	 * */
	public void deleteJob(JobTaskInfo jobTask)throws SchedulerException{
		JobKey jobKey = JobKey.jobKey(jobTask.getTaskName(), jobTask.getTaskGroup());
		if (jobKey != null)
			logger.info("job调度器开始删除任务 jobKey = "+jobKey);
			scheduler.deleteJob(jobKey);
	}
	
	/**
	 * 删除任务并且移除触发器
	 * @param jobTask
	 * */
	public void deleteTriggerAndJob(JobTaskInfo jobTask)throws SchedulerException{
		JobKey jobKey = JobKey.jobKey(jobTask.getTaskName(), jobTask.getTaskGroup());
		if (jobKey != null){
			logger.info("job调度器开始删除任务并删除触发器 jobKey = "+jobKey);
			TriggerKey triggerKey = TriggerKey.triggerKey(jobTask.getTaskName(),jobTask.getTaskGroup());
			scheduler.pauseTrigger(triggerKey);//停住触发器
			scheduler.unscheduleJob(triggerKey);//移除触发器
			scheduler.deleteJob(jobKey);//删除任务
		}
	}
	
	/**
	 * 启动任务调度器
	 * */
	public void startJobs()throws SchedulerException {  
    	if (scheduler.isShutdown()) {  
    		logger.info("job调度器scheduler开始启动...");
    		scheduler.start();  
    	}
    }  
  
    /** 
     * 停止任务调度器
     */  
    public void shutdownJobs()throws SchedulerException{
        if (!scheduler.isShutdown()) {  
        	logger.info("job调度器scheduler开始停止...");
        	scheduler.shutdown();  
        }  
    }  

}
