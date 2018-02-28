package com.dw.suppercms.application.crontab.job;

import java.io.File;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dw.suppercms.application.crontab.scheduler.JobSchedulerCentre;
import com.dw.suppercms.application.index.IndexSearchService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.application.system.JobTaskService;
import com.dw.suppercms.application.system.SysParamService;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.domain.system.JobTaskLogInfo;
import com.dw.suppercms.domain.system.JobTaskLogInfo.TASK_RESULT;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.utils.SpringContextUtil;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.rule.CmsFileUtils;
import com.dw.suppercms.produce.MakeFileService;

/**
 * 作业类抽象封装
 * @author kobe
 * */
public abstract class AbstractQuartzJob extends QuartzJobBean {
	
	protected static Logger logger=LoggerFactory.getLogger(AbstractQuartzJob.class);
	
	protected MakeFileService makeFileService;
	
	protected ProduceFileService produceFileService;
	
	protected JobTaskService jobTaskService;
	
	protected SysParamService sysParamService;
	
	protected IndexSearchService indexSearchService;
	
	public AbstractQuartzJob(){
		makeFileService=(MakeFileService) SpringContextUtil.getBean("makeFileService");
		produceFileService=(ProduceFileService) SpringContextUtil.getBean("produceFileService");
		jobTaskService=(JobTaskService) SpringContextUtil.getBean("jobTaskService");
		sysParamService=(SysParamService) SpringContextUtil.getBean("sysParamService");
		indexSearchService=(IndexSearchService) SpringContextUtil.getBean("indexSearchService");
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		this.setExeDate(new Date());
		//获取参数
		JobDataMap dataMap=context.getMergedJobDataMap();
		JobTaskInfo jobTask=(JobTaskInfo) dataMap.get(JobSchedulerCentre.JobDataMapKey);
		
		this.setParams(jobTask.getTaskTarget());
		this.setTaskId(jobTask.getId());
		logger.info("后台自定义定时任务开始执行【"+JobTaskInfo.typeToChar(jobTask.getTaskType())+"】："+jobTask.getTaskName());
		
		runTimingTask(context);
	}
	
	protected abstract void runTimingTask(JobExecutionContext context) throws JobExecutionException;
	
	/**
	 * 常规的任务结果处理
	 * */
	protected void stdTaskResultProcress(MakeFileResult result){
		String exeMessage = result.getMakeErrorMsg();
		TASK_RESULT taskResult=TASK_RESULT.FAIL;
		try {
			if (result.getMakeResult().equals(MAKE_RESULT.SUCCESS)) {
				taskResult=TASK_RESULT.SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			String consuming=CommonsUtil.formateTimes((System.currentTimeMillis()-this.getExeDate().getTime()));
			this.saveJobTaskLog(taskResult, exeMessage,consuming);
		}
	}
	
	/**
	 * 同步生成的文件
	 * @param isDirectory：是否文件夹
	 * */
	protected void syncMakeFile(String src,String desc,boolean isDirectory){
		if(!isDirectory){
			desc = src.substring(0, src.lastIndexOf("/"));
		}
		new File(desc).mkdirs();
		CmsFileUtils.sysncDirs(src, desc);
	}
	
	/**
	 * 保存任务日志
	 * */
	protected void saveJobTaskLog(TASK_RESULT taskResult,String exeMessage,String consuming){
		JobTaskLogInfo log=new JobTaskLogInfo();
		log.setTaskId(taskId);
		log.setExeTime(exeDate);
		log.setExeMessage(exeMessage);
		log.setExeResult(taskResult);
		log.setConsuming(consuming);
		jobTaskService.creareJobTaskLog(log);
	}
	
	
	@Setter
	@Getter
	private Date exeDate;
	@Setter
	private Long taskId;
	@Setter
	@Getter
	private String params;

}
