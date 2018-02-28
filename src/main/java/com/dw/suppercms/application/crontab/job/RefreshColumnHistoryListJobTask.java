package com.dw.suppercms.application.crontab.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_KEY;
import com.dw.suppercms.produce.MakeFileResult;

/**
 * 刷新栏目历史列表任务
 * @author kobe
 * */
public class RefreshColumnHistoryListJobTask extends AbstractQuartzJob{
	
	public static final Long historyListTaskId=-99999L;//历史列表页的任务ID
    private static String isProduceListHistory="true";//默认生成
	public static final String cronExpression_month="0 0 3 1 * ?";//默认每月一号凌晨三点执行生成
	public static final String cronExpression_week="0 0 3 ? * MON";//每周一凌晨三点执行

	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		String paramVal=this.sysParamService.findSysParamValByCodeAndKey(PARAM_CODE.produce, PARAM_KEY.isProduceHistory.toString());
		if(StringUtils.isNotEmpty(paramVal)){
			isProduceListHistory=paramVal;
		}
		if("true".equals(isProduceListHistory)){
			List<Column> columnList=this.produceFileService.findAllColumns();
			logger.info("开始执行-生成栏目历史列表数据,生成方式【按月】生成...");
			MakeFileResult result=makeFileService.makeHistoryListFileTask(columnList, 1);//查询上个月的数据
			setTaskId(historyListTaskId);
			stdTaskResultProcress(result);
		}
	}

	
}
