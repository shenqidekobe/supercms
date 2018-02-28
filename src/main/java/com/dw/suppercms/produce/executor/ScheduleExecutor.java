package com.dw.suppercms.produce.executor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.listener.event.MakeFileTask;

/**
 * 调度生成任务
 * */
@Service
public class ScheduleExecutor {
	
	@Resource
	private CallableTransform callableTransform;
	
	//定义四个线程池，分别为生成首页文件、生成列表文件、生成内容页文件、生成自定义页文件线程池
	@Resource
	private  ExecutorService makeIndexExecutorService;
	@Resource
	private  ExecutorService makeListExecutorService;
	@Resource
	private  ExecutorService makeContentExecutorService;
	@Resource
	private  ExecutorService makeCustomExecutorService;
	
	/**
	 * 执行生成站点首页任务
	 * */
	public CompletionService<MakeFileResult> executeSiteIndexTask(List<Site> sites) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeIndexExecutorService);
		for (Site site : sites) {
			MakeFileTask makeFileTask=callableTransform.SiteTransformMakeIndexFileTask(site);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}

	/**
	 * 执行单个栏目的生成内容任务
	 * */
	public CompletionService<MakeFileResult> executeColumnContentTask(Column column, List<DataRecord> dataRecords) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeContentExecutorService);
		for (DataRecord data : dataRecords) {
			MakeFileTask makeFileTask=callableTransform.ColumnTransformMakeContentFileTask(column,data);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}
	
	/**
	 * 执行一条数据的多个栏目生成任务
	 * */
	public CompletionService<MakeFileResult> executeColumnContentTask(DataRecord dataRecord, List<Column> columns) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeContentExecutorService);
		for (Column column : columns) {
			MakeFileTask makeFileTask=callableTransform.ColumnTransformMakeContentFileTask(column,dataRecord);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}
	
	/**
	 * 执行多个栏目的生成内容任务
	 * */
	public CompletionService<MakeFileResult> executeColumnContentTask(List<Column> columns, Map<Column,List<DataRecord>> dataMap) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeContentExecutorService);
		for(Column column:columns){
			List<DataRecord> dataRecords=dataMap.get(column);
			for (DataRecord data : dataRecords) {
				MakeFileTask makeFileTask=callableTransform.ColumnTransformMakeContentFileTask(column,data);
				Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
				completionService.submit(callable);
			}
		}
		return completionService;
	}

	/**
	 * 执行生成栏目列表任务
	 * */
	public CompletionService<MakeFileResult> executeColumnListTask(List<Column> columns,Map<String,Object> paramMap) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeListExecutorService);
		for (Column column : columns) {
			MakeFileTask makeFileTask=callableTransform.ColumnTransformMakeListFileTask(column,paramMap);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}
	
	/**
	 * 执行生成栏目列表任务
	 * */
	public CompletionService<MakeFileResult> executeColumnListHistoryTask(List<Column> columns,Integer preCount) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeListExecutorService);
		for (Column column : columns) {
			MakeFileTask makeFileTask=callableTransform.ColumnTransformMakeHistoryListFileTask(column,preCount);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}
	
	/**
	 * 执行生成自定义任务
	 * */
	public CompletionService<MakeFileResult> executeCustomTask(List<Custom> customs) {
		CompletionService<MakeFileResult> completionService = new ExecutorCompletionService<MakeFileResult>(makeCustomExecutorService);
		for (Custom custom : customs) {
			MakeFileTask makeFileTask=callableTransform.CustomTransformMakeCustomFileTask(custom);
			Callable<MakeFileResult> callable=callableTransform.call(makeFileTask);
			completionService.submit(callable);
		}
		return completionService;
	}


}
