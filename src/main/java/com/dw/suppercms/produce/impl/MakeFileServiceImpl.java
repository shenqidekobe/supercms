package com.dw.suppercms.produce.impl;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.application.system.ProduceLogService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_ERROR_CODE;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.MakeFileService;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.execption.MakeContentExecption;
import com.dw.suppercms.produce.execption.MakeCustomExecption;
import com.dw.suppercms.produce.execption.MakeIndexExecption;
import com.dw.suppercms.produce.execption.MakeListExecption;
import com.dw.suppercms.produce.executor.CallableTransform;
import com.dw.suppercms.produce.executor.ScheduleExecutor;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.FreemarkerUtils;
import com.dw.suppercms.produce.rule.MakeFileCommon;
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 生成文件实现类
 * */
@Service
public class MakeFileServiceImpl implements MakeFileService{
	
	@Resource
	private ProduceFileService produceFileService;
	
	@Resource
	private ProduceLogService produceLogService;
	
	@Resource
	private ScheduleExecutor scheduleExecutor;
	
	@Resource
	private MakeFileCommon makeFileCommon;
	
	@Resource
	private CallableTransform callableTransform;
	

	@Override
	public MakeFileResult makeIndexFile(Site site) throws MakeIndexExecption {
		MakeFileResult makeFileResult=null;
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeSiteIndexTask(Arrays.asList(site));
		try {
			Future<MakeFileResult> future = completionService.take();
			makeFileResult=future.get();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			this.saveProduceLog(makeFileResult);
		}
		return makeFileResult;
	}


	@Override
	public CompletionService<MakeFileResult> makeIndexFile(List<Site> sites)
			throws MakeIndexExecption {
		return this.scheduleExecutor.executeSiteIndexTask(sites);
	}
	
	@Override
	public MakeFileResult makeIndexFileTask(List<Site> sites)
			throws MakeIndexExecption {
		 CompletionService<MakeFileResult> completionService=this.makeIndexFile(sites);
		 return completionServiceTakeResults(sites.size(), completionService,"批量刷新首页任务");
	}



	@Override
	public MakeFileResult makeListFile(Column column) throws MakeListExecption {
		MakeFileResult makeFileResult=null;
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeColumnListTask(Arrays.asList(column),null);
		try {
			Future<MakeFileResult> future = completionService.take();
			makeFileResult=future.get();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			this.saveProduceLog(makeFileResult);
		}
		return makeFileResult;
	}


	@Override
	public CompletionService<MakeFileResult> makeListFile(List<Column> columns,Map<String,Object> paramMap)
			throws MakeListExecption {
		return this.scheduleExecutor.executeColumnListTask(columns,paramMap);
	}
	
	@Override
	public MakeFileResult makeListFileTask(List<Column> columns,Map<String,Object> paramMap)
			throws MakeListExecption {
		CompletionService<MakeFileResult> completionService=this.makeListFile(columns,paramMap);
		return completionServiceTakeResults(columns.size(), completionService,"批量刷新栏目列表任务");
	}

	@Override
	public CompletionService<MakeFileResult> makeHistoryListFile(Column column,Integer preCount) throws MakeListExecption {
		return this.scheduleExecutor.executeColumnListHistoryTask(Arrays.asList(column),preCount);
	}


	@Override
	public MakeFileResult makeHistoryListFileTask(Column column,Integer preCount)
			throws MakeListExecption {
		CompletionService<MakeFileResult> completionService=this.makeHistoryListFile(column,preCount);
		return completionServiceTakeResults(1, completionService,"刷新栏目历史列表任务");
	}
	
	public MakeFileResult makeHistoryListFileTask(List<Column> columns,Integer preCount)throws MakeListExecption{
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeColumnListHistoryTask(columns,preCount);
		return completionServiceTakeResults(columns.size(), completionService,"批量刷新栏目历史列表任务");
	}
	

	@Override
	public MakeFileResult makeContentFileTask(DataRecord dataRecord,List<Column> columns)
			throws MakeContentExecption {
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeColumnContentTask(dataRecord, columns);
		return completionServiceTakeResults(columns.size(), completionService, "手动执行单挑数据的多个栏目最终页生成,数据ID="+dataRecord.getId());
	}
	
	@Override
	public CompletionService<MakeFileResult> makeContentFile(DataRecord dataRecord,List<Column> columns)
			throws MakeContentExecption {
		return this.scheduleExecutor.executeColumnContentTask(dataRecord, columns);
	}


	@Override
	public MakeFileResult makeContentFile(Column column, DataRecord dataRecord)
			throws MakeContentExecption {
		MakeFileResult makeFileResult=null;
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeColumnContentTask(column, Arrays.asList(dataRecord));
		try {
			Future<MakeFileResult> future = completionService.take();
			makeFileResult=future.get();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			this.saveProduceLog(makeFileResult);
		}
		return makeFileResult;
	}


	@Override
	public MakeFileResult makeContentFile(Column column,
			List<DataRecord> dataRecords) throws MakeContentExecption {
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeColumnContentTask(column, dataRecords);
		return completionServiceTakeResults(dataRecords.size(), completionService, "手动执行单个栏目的所有最终页生成,栏目ID="+column.getId());
	}

	
	@Override
	public CompletionService<MakeFileResult> makeContentFileGoCompletion(Column column,List<DataRecord> dataRecords)throws MakeContentExecption{
		return this.scheduleExecutor.executeColumnContentTask(column, dataRecords);
	}

	@Override
	public CompletionService<MakeFileResult> makeContentFile(
			List<Column> columns, Map<Column, List<DataRecord>> dataMap)
			throws MakeContentExecption {
		return this.scheduleExecutor.executeColumnContentTask(columns, dataMap);
	}
	
	@Override
	public MakeFileResult makeContentFileTask(
			List<Column> columns, Map<Column, List<DataRecord>> dataMap)
			throws MakeContentExecption {
		CompletionService<MakeFileResult> completionService= this.makeContentFile(columns, dataMap);
		int makeCount=0;
		for(Column column:columns){
			List<DataRecord> dataRecords=dataMap.get(column);
			makeCount+=dataRecords.size();
		}
		return completionServiceTakeResults(makeCount, completionService, "批量刷新栏目内容任务");
	}
	
	@Override
	public MakeFileResult makeContentAndListFileTask(List<Column> columns,Map<Column,List<DataRecord>> dataMap)throws MakeContentExecption{
		MakeFileResult makeFileResult=null;
		Date startMakeTime=new Date();
		CompletionService<MakeFileResult> completionService=this.makeContentFile(columns, dataMap);
		int makeCount=0;
		for(Column column:columns){
			List<DataRecord> dataRecords=dataMap.get(column);
			makeCount+=dataRecords.size();
		}
		int columnSize=columns.size();
		try {
			int successCount=0,failCount=0;
			String errorMsg="";
			int errorCount=0;
			for (int i = 0; i < makeCount; i++) {
				Future<MakeFileResult> future = completionService.take();
				MakeFileResult result = future.get();
				if(result.getMakeResult().equals(MAKE_RESULT.SUCCESS)){
					successCount++;
				}else{
					failCount++;
					errorMsg+=result.getMakeErrorMsg();
				}
				if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
					errorCount++;
				}
			}
			CompletionService<MakeFileResult> completionService2=this.makeListFile(columns,null);
			for (int i = 0; i < columnSize; i++) {
				Future<MakeFileResult> future = completionService2.take();
				MakeFileResult result = future.get();
				if(result.getMakeResult().equals(MAKE_RESULT.SUCCESS)){
					successCount++;
				}else{
					failCount++;
					errorMsg+=result.getMakeErrorMsg();
				}
				if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
					errorCount++;
				}
			}
			MAKE_RESULT makeResult=MAKE_RESULT.SUCCESS;
			if(errorCount!=0&&errorCount<makeCount){
				makeResult=MAKE_RESULT.PART_SUCCESS;
			}else if(makeCount!=0&&errorCount==makeCount){
				makeResult=MAKE_RESULT.FAIL;
			}
			makeFileResult=new MakeFileResult(MakeEventType.CONTENT,makeResult, makeCount, successCount, failCount);
			makeFileResult.setStartMakeTime(startMakeTime);
			makeFileResult.setMakeErrorMsg(errorMsg);
			makeFileResult.setMakeDesc("批量刷新栏目内容、列表任务");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			this.saveProduceLog(makeFileResult);
		}
		return makeFileResult;
	}


	@Override
	public MakeFileResult makeCustomFile(Custom custom)
			throws MakeCustomExecption {
		MakeFileResult makeFileResult=null;
		CompletionService<MakeFileResult> completionService=this.scheduleExecutor.executeCustomTask(Arrays.asList(custom));
		try {
			Future<MakeFileResult> future = completionService.take();
			makeFileResult=future.get();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}finally{
			this.saveProduceLog(makeFileResult);
		}
		return makeFileResult;
	}


	@Override
	public CompletionService<MakeFileResult> makeCustomFile(List<Custom> customs)
			throws MakeCustomExecption {
		return this.scheduleExecutor.executeCustomTask(customs);
	}
	
	@Override
	public MakeFileResult makeCustomFileTask(List<Custom> customs)
			throws MakeCustomExecption {
		CompletionService<MakeFileResult> completionService=this.makeCustomFile(customs);
		return completionServiceTakeResults(customs.size(), completionService, "批量刷新自定义任务");
	}


	@Override
	public MakeFileResult makeAllFile() throws MakeIndexExecption,
			MakeListExecption, MakeContentExecption, MakeCustomExecption {
		List<Site> sites=this.produceFileService.findAllSites();
		List<Column> columns=this.produceFileService.findAllColumns();
		List<Custom> customs=this.produceFileService.findAllCustoms();
		List<MakeFileResult> results=new ArrayList<MakeFileResult>();
		try {
			MakeFileResult makeFileResult=null;
			CompletionService<MakeFileResult> completionService1=this.scheduleExecutor.executeCustomTask(customs);
			for(int i=0;i<customs.size();i++){
				Future<MakeFileResult> future = completionService1.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
			
			for(Column column:columns){
				Map<String,Object> params=new HashMap<>();
				params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_producenot);
				Pager pager=new Pager(0, 10000);
				SearchResult<DataRecord> srs=this.produceFileService.findDataRecordListUseDirective(params,Arrays.asList(column.getId()), null, null, pager);
				CompletionService<MakeFileResult> completionService2=this.scheduleExecutor.executeColumnContentTask(column, srs.getResult());
				for(int i=0;i<srs.getTotalCount();i++){
					Future<MakeFileResult> future = completionService2.take();
					makeFileResult=future.get();
					results.add(makeFileResult);
				}
			}
			
			CompletionService<MakeFileResult> completionService3=this.scheduleExecutor.executeColumnListTask(columns,null);
			for(int i=0;i<columns.size();i++){
				Future<MakeFileResult> future = completionService3.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
			
			CompletionService<MakeFileResult> completionService4=this.scheduleExecutor.executeSiteIndexTask(sites);
			for(int i=0;i<sites.size();i++){
				Future<MakeFileResult> future = completionService4.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return makeFileResultListToResult(results, "生成系统所有的文件");
	}


	@Override
	public MakeFileResult makeSiteTotalFile(Site site)
			throws MakeIndexExecption, MakeListExecption, MakeContentExecption,
			MakeCustomExecption {
		List<Custom> customs=this.produceFileService.findAllCustoms();
		CompletionService<MakeFileResult> completionService1=this.scheduleExecutor.executeCustomTask(customs);
		List<MakeFileResult> results=new ArrayList<MakeFileResult>();
		try {
			MakeFileResult makeFileResult=null;
			for(int i=0;i<customs.size();i++){
				Future<MakeFileResult> future = completionService1.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
			List<Column> columns=this.produceFileService.findColumnBySiteId(site.getId());
			for(Column column:columns){
				Map<String,Object> params=new HashMap<>();
				params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_producenot);
				Pager pager=new Pager(0, 10000);
				SearchResult<DataRecord> srs=this.produceFileService.findDataRecordListUseDirective(params,Arrays.asList(column.getId()), null, null, pager);
				CompletionService<MakeFileResult> completionService2=this.scheduleExecutor.executeColumnContentTask(column, srs.getResult());
				for(int i=0;i<srs.getTotalCount();i++){
					Future<MakeFileResult> future = completionService2.take();
					makeFileResult=future.get();
					results.add(makeFileResult);
				}
			}
			
			CompletionService<MakeFileResult> completionService3=this.scheduleExecutor.executeColumnListTask(new ArrayList<Column>(columns),null);
			for(int i=0;i<columns.size();i++){
				Future<MakeFileResult> future = completionService3.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
			
			CompletionService<MakeFileResult> completionService4=this.scheduleExecutor.executeSiteIndexTask(Arrays.asList(site));
			Future<MakeFileResult> future = completionService4.take();
			makeFileResult=future.get();
			results.add(makeFileResult);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return 	makeFileResultListToResult(results, "生成站点的所有文件："+site.getId());
	}


	@Override
	public MakeFileResult makeColumnTotalFile(Column column)
			throws MakeListExecption, MakeContentExecption {
		List<MakeFileResult> results=new ArrayList<MakeFileResult>();
		try {
			MakeFileResult makeFileResult=null;
			Map<String,Object> params=new HashMap<>();
			params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_producenot);
			Pager pager=new Pager(0, 10000);
			SearchResult<DataRecord> srs=this.produceFileService.findDataRecordListUseDirective(params,Arrays.asList(column.getId()), null, null, pager);
			CompletionService<MakeFileResult> completionService2=this.scheduleExecutor.executeColumnContentTask(column, srs.getResult());
			for(int i=0;i<srs.getTotalCount();i++){
				Future<MakeFileResult> future = completionService2.take();
				makeFileResult=future.get();
				results.add(makeFileResult);
			}
			
			CompletionService<MakeFileResult> completionService3=this.scheduleExecutor.executeColumnListTask(Arrays.asList(column),null);
			Future<MakeFileResult> future = completionService3.take();
			makeFileResult=future.get();
			results.add(makeFileResult);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return makeFileResultListToResult(results, "生成栏目的内容列表："+column.getId());
	}


	@Override
	public InputStream previewContentFile(DataRecord dataRecord, Column column)
			throws MakeContentExecption {
		Writer output=FreemarkerUtils.parseContentTemplate(callableTransform.ColumnTransformMakeContentFileTask(column, dataRecord), makeFileCommon.bulidBasicSimpleHash(), makeFileCommon.getConfiguration());
		ByteArrayInputStream arrayInputStream=new ByteArrayInputStream(output.toString().getBytes());
		return arrayInputStream;
	}
	
	/**
	 * 保存生成日志信息
	 * */
	private void saveProduceLog(MakeFileResult makeFileResult){
		assertNotNull(makeFileResult);
		ProduceLogInfo produceLog=new ProduceLogInfo();
		try {
			User user=CommonsUtil.getPrincipal();
			produceLog.setUserId(user==null?null:user.getId());
		} catch (Exception e) {}
		produceLog.setProduceType(makeFileResult.getMakeType().getKey());
		produceLog.setProduceResult(makeFileResult.getMakeResult().name());
		produceLog.setProduceDesc(makeFileResult.getMakeDesc());
		produceLog.setProduceDate(makeFileResult.getStartMakeTime());
		produceLog.setProduceCount(makeFileResult.getMakeCount());
		produceLog.setProduceSuccessCount(makeFileResult.getSuccessCount());
		produceLog.setProduceFailCount(makeFileResult.getFailCount());
		produceLog.setProduceResultMsg(makeFileResult.getMakeErrorMsg());
		produceLogService.createProduceLog(produceLog);
	}
	
	/**
	 * 任务completeService获取结果
	 * */
	private MakeFileResult completionServiceTakeResults(int makeCount,CompletionService<MakeFileResult> completionService,String taskDesc){
		 MakeFileResult makeFileResult=null;
		 Date startMakeTime=new Date();
		 Set<Site> successSites=new HashSet<Site>();
		 Set<Column> successColumns=new HashSet<Column>();
		 Set<Custom> successCustoms=new HashSet<Custom>();
		 try {
				int successCount=0,failCount=0;
				String errorMsg="";
				int errorCount=0;
				MakeEventType eventType=null;
				for(int i=0;i<makeCount;i++){
					Future<MakeFileResult> future = completionService.take();
					MakeFileResult result=future.get();
					if(result.getMakeResult().equals(MAKE_RESULT.SUCCESS)){
						successSites.add(result.getSite());
						successColumns.add(result.getColumn());
						successCustoms.add(result.getCustom());
						successCount++;
					}else{
						failCount++;
						errorMsg+=result.getMakeErrorMsg();
					}
					if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
						errorCount++;
					}
					eventType=result.getMakeType();
				}
				MAKE_RESULT makeResult=MAKE_RESULT.SUCCESS;
				if(errorCount!=0&&errorCount<makeCount){
					makeResult=MAKE_RESULT.PART_SUCCESS;
				}else if(makeCount!=0&&errorCount==makeCount){
					makeResult=MAKE_RESULT.FAIL;
				}
				makeFileResult=new MakeFileResult(eventType,makeResult, makeCount, successCount, failCount);
				makeFileResult.setStartMakeTime(startMakeTime);
				makeFileResult.setMakeErrorMsg(errorMsg);
				makeFileResult.setMakeDesc(taskDesc);
				makeFileResult.setSuccessSites(successSites);
				makeFileResult.setSuccessColumns(successColumns);
				makeFileResult.setSuccessCustoms(successCustoms);
		 } catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		 }finally{
			this.saveProduceLog(makeFileResult);
		 }
		 return makeFileResult;
	}
	
	/**
	 * 结果集合转换为一个生成结果
	 * */
	private MakeFileResult makeFileResultListToResult(List<MakeFileResult> results,String taskDesc){
		MakeFileResult makeFileResult=null;
		 Date startMakeTime=new Date();
		 try {
				int successCount=0,failCount=0,makeCount=results.size();
				String errorMsg="";
				int errorCount=0;
				for(MakeFileResult result:results){
					if(result.getMakeResult().equals(MAKE_RESULT.SUCCESS)){
						successCount++;
					}else{
						failCount++;
						errorMsg+=result.getMakeErrorMsg();
					}
					if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
						errorCount++;
					}
				}
				MAKE_RESULT makeResult=MAKE_RESULT.SUCCESS;
				if(errorCount!=0&&errorCount<makeCount){
					makeResult=MAKE_RESULT.PART_SUCCESS;
				}else if(makeCount!=0&&errorCount==makeCount){
					makeResult=MAKE_RESULT.FAIL;
				}
				makeFileResult=new MakeFileResult(MakeEventType.CONTENT,makeResult, makeCount, successCount, failCount);
				makeFileResult.setStartMakeTime(startMakeTime);
				makeFileResult.setMakeErrorMsg(errorMsg);
				makeFileResult.setMakeDesc(taskDesc);
				
		 } catch (Exception e) {
			e.printStackTrace();
		 }finally{
			this.saveProduceLog(makeFileResult);
		 }
		 return makeFileResult;
	}

}
