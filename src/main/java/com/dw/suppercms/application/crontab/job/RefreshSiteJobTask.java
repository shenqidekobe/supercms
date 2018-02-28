package com.dw.suppercms.application.crontab.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.system.JobTaskInfo;
import com.dw.suppercms.produce.MakeFileResult;

/**
 * 刷新站点任务
 * @author kobe
 * */
public class RefreshSiteJobTask extends AbstractQuartzJob{
	
	@Override
	protected void runTimingTask(JobExecutionContext context) throws JobExecutionException {
		List<Site> sites=null;
		if(JobTaskInfo.ALL_IDENTIFY.equals(getParams())){
			sites=produceFileService.findAllSites();
		}else{
			sites=produceFileService.findSiteByIds(getParams());
		}
		if(sites.isEmpty())return;
		MakeFileResult makeFileResult=makeFileService.makeIndexFileTask(sites);
		stdTaskResultProcress(makeFileResult);
		
		//执行成功的站点首同步到发布目录
		for(Site site:makeFileResult.getSuccessSites()){
			String src=site.getFileDiskpath();
			String desc=site.getPubFileDiskpath();
			syncMakeFile(src, desc, true);
		}
	}

}
