package com.dw.suppercms.produce.executor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFutureTask;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_ERROR_CODE;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.execption.MakeContentExecption;
import com.dw.suppercms.produce.execption.MakeCustomExecption;
import com.dw.suppercms.produce.execption.MakeHistoryListExecption;
import com.dw.suppercms.produce.execption.MakeIndexExecption;
import com.dw.suppercms.produce.execption.MakeListExecption;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeTemplateFileEvent;

/**
 * 任务转换中心
 * */
@Service
public class CallableTransform {
	
	@Resource
	private ApplicationContext applicationContext;

	/**
	 * 创建Callable任务
	 * */
	public Callable<MakeFileResult> call(final MakeFileTask makeFileTask){
		final ApplicationContext appContext = applicationContext;
		Callable<MakeFileResult> callable=new Callable<MakeFileResult>(){
			@Override
			public MakeFileResult call() throws Exception {
				MakeFileResult makeFileResult=new MakeFileResult();
				makeFileResult.setMakeType(makeFileTask.getMakeEventType());
				makeFileResult.setColumn(makeFileTask.getColumn());
				makeFileResult.setSite(makeFileTask.getSite());
				makeFileResult.setCustom(makeFileTask.getCustom());
				makeFileResult.setFilePath(makeFileTask.getFilePath());
				if(makeFileTask.getMakeEventType().equals(MakeFileTask.MakeEventType.CONTENT)){
					makeFileResult.setDataId(makeFileTask.getDataRecord().getId());
					makeFileResult.setDataTitle(makeFileTask.getDataRecord().getTitle());
					makeFileResult.setId(makeFileTask.getColumn().getId());
					makeFileResult.setTitle(makeFileTask.getColumn().getTitle());
				}else{
					makeFileResult.setId(makeFileTask.getFileId());
					makeFileResult.setTitle(makeFileTask.getSourceName());
				}
				try {
					//发布生成任务
					appContext.publishEvent(new MakeTemplateFileEvent(this, makeFileTask));
				} catch( Throwable e){
					if(e.getClass().equals(MakeIndexExecption.class)||e.getClass().equals(MakeListExecption.class)
							||e.getClass().equals(MakeContentExecption.class)||e.getClass().equals(MakeCustomExecption.class)
							||e.getClass().equals(MakeHistoryListExecption.class)){
						makeFileResult.setMakeErrorCode(MAKE_ERROR_CODE.RUNTIME_EXCEPTION);
					}
					makeFileResult.setMakeErrorMsg(e.getClass()+":"+e.getMessage());
					makeFileResult.setMakeResult(MakeFileResult.MAKE_RESULT.FAIL);
				}
				return makeFileResult;
			}
		};
		return callable;
	}
	
	/**
	 * 创建listenableFutureTask任务
	 * can add callback event,for example:
	 * listenableFutureTask.addCallback(new ListenableFutureCallback<MakeFileResult>() {  
	        @Override  
	        public void onSuccess(MakeFileResult makeFileResult) {  
	        }  
	  
	        @Override  
	        public void onFailure(Throwable t) {  
	        }  
	    });
	 * */
	public ListenableFutureTask<MakeFileResult> futureTask(final MakeFileTask makeFileTask){
		final ApplicationContext appContext = applicationContext;
		ListenableFutureTask<MakeFileResult> listenableFutureTask=new ListenableFutureTask<MakeFileResult>(new Callable<MakeFileResult>(){
			@Override
			public MakeFileResult call() throws Exception {
				MakeFileResult makeFileResult=new MakeFileResult();
				try {
					appContext.publishEvent(new MakeTemplateFileEvent(this, makeFileTask));
				} catch( Throwable e){
					makeFileResult.setMakeErrorMsg(e.getMessage());
					makeFileResult.setMakeResult(MakeFileResult.MAKE_RESULT.FAIL);
				}
				return makeFileResult;
			}
		});
		return listenableFutureTask;
	}
	
	/**
	 * 站点转换为首页文件生成任务
	 * */
	public MakeFileTask SiteTransformMakeIndexFileTask(Site site){
		Assert.notNull(site);
		MakeFileTask makeFileTask=new MakeFileTask(MakeFileTask.MakeEventType.INDEX);
		makeFileTask.setFileId(site.getId());
		makeFileTask.setFilePath(site.getFileDiskpath());
		makeFileTask.setFileName(site.getFileName());
		makeFileTask.setSourceName(site.getTitle());
		makeFileTask.setWebId(site.getId());
		makeFileTask.setTemplateId(site.getHomeTemplateId());
		makeFileTask.setSite(site);
		return makeFileTask;
	}
	
	/**
	 * 栏目转换为列表文件生成任务
	 * */
	public MakeFileTask ColumnTransformMakeListFileTask(Column column,Map<String,Object> paramMap){
		Assert.notNull(column);
		Assert.notNull(column.getDatasource());
		Assert.notNull(column.getSite());
		MakeFileTask makeFileTask=new MakeFileTask(MakeFileTask.MakeEventType.LISTS);
		makeFileTask.setWebId(column.getSite().getId());
		makeFileTask.setFileId(column.getId());
		makeFileTask.setColumn(column);
		makeFileTask.setSourceName(column.getTitle());
		makeFileTask.setTemplateId(column.getHomeTemplateId());
		makeFileTask.setParamMap(paramMap);
		return makeFileTask;
	}
	
	/**
	 * 栏目转换为历史列表页生成任务
	 * */
	public MakeFileTask ColumnTransformMakeHistoryListFileTask(Column column,Integer preCount){
		MakeFileTask makeFileTask=ColumnTransformMakeListFileTask(column,null);
		makeFileTask.setMakeEventType(MakeFileTask.MakeEventType.HISTORY);
		makeFileTask.setPreCount(preCount);
		return makeFileTask;
	}
	
	/**
	 * 栏目转换为内容文件生成任务
	 * */
	public MakeFileTask ColumnTransformMakeContentFileTask(Column column,DataRecord dataRecord){
		Assert.notNull(column);
		Assert.notNull(column.getDatasource());
		Assert.notNull(column.getSite());
		MakeFileTask makeFileTask=new MakeFileTask(MakeFileTask.MakeEventType.CONTENT);
		String contentPath=column.getContentFilePath(dataRecord.getId(), new Timestamp(dataRecord.getTime().getTime()));
		makeFileTask.setWebId(column.getSite().getId());
		makeFileTask.setFileId(column.getId());
		makeFileTask.setSourceName(column.getTitle());
		makeFileTask.setDataRecord(dataRecord);
		makeFileTask.setColumn(column);
		makeFileTask.setTemplateId(column.getContentTemplateId());
		makeFileTask.setFilePath(contentPath);
		return makeFileTask;
	}
	
	/**
	 * 自定义页转换为文件生成任务
	 * */
	public MakeFileTask CustomTransformMakeCustomFileTask(Custom custom){
		Assert.notNull(custom);
		MakeFileTask makeFileTask=new MakeFileTask(MakeFileTask.MakeEventType.CUSTOMS);
		makeFileTask.setFileId(custom.getId());
		makeFileTask.setFilePath(custom.getFileDiskpath());
		makeFileTask.setFileName(custom.getFileName());
		makeFileTask.setSourceName(custom.getTitle());
		makeFileTask.setTemplateId(custom.getCustomTemplateId());
		makeFileTask.setCustom(custom);
		return makeFileTask;
	}

}
