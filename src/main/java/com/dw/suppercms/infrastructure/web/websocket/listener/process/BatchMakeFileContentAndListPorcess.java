package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;
import com.dw.suppercms.infrastructure.web.websocket.dto.MakeProcess;
import com.dw.suppercms.infrastructure.web.websocket.listener.BatchMakeFileScheduleListener;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_ERROR_CODE;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.googlecode.genericdao.search.SearchResult;

import flexjson.JSONSerializer;

/**
 * 批量更新栏目的内容页和列表页信息
 * */
@Service
public class BatchMakeFileContentAndListPorcess extends BatchMakeFileScheduleListener {

	@Override
	protected void execute(BatchTask task) {
		Long b = System.currentTimeMillis();
		String columnIds=task.getParams();
		List<MakeProcess> errorProcessList = new ArrayList<MakeProcess>();
		String[] split = columnIds.split(",");
		MakeProcess process = null;
		log.info("批量生成-刷新内容页和列表页：id=" + columnIds);
		//定义记录生成日志的变量
		int makeCount=0;
		int successCount=0;
		int failCount=0;
		int errorCount=0;
		
		//定义输出进度的变量
		int length=split.length;
		int defaultEvery=10000;
		int totalCount=length*defaultEvery+length;
		int tempCount=0;
		boolean flag=true;
		for (int i = 0; i < length; i++) {
			flag=true;
			Long columnId=Long.valueOf(split[i]);
			Column column = produceFileService.findColumnById(columnId);
			Pager pager = new Pager();
			pager.setPageSize(50);
			Map<String,Object> params=new HashMap<>();
			SearchResult<DataRecord> sr=produceFileService.findDataRecordListUseDirective(params, Arrays.asList(columnId), null, null, pager);
			
			List<DataRecord> list=sr.getResult();
			int count=sr.getTotalCount();//总记录数量
			pager.calcPageCount((long) count);
			if (count != 0) {
				makeCount+=count;
				CompletionService<MakeFileResult> completionService = makeFileService.makeContentFileGoCompletion(column, list);
				for (int j = 1; j <= count; j++) {
					MakeFileResult result = null;
					int percent = 0;
					try {
						Future<MakeFileResult> future = completionService.take();
						result = future.get();
						if(result.getMakeResult().equals(MakeFileResult.MAKE_RESULT.SUCCESS)){
							successCount++;
						}else{
							failCount++;
						}
						if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
							errorCount++;
						}
						//计算临时处理大小和总数的差距
						if(count<=defaultEvery){
							if(flag){
								tempCount+=defaultEvery-count;
								flag=false;
							}
						}else{
							if(flag){
								totalCount+=count-defaultEvery;
								flag=false;
							}
						}
						percent = Math.round((tempCount) * 1f / totalCount * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} finally {
						process = new MakeProcess();
						String id = result.getDataId();
						String name = result.getDataTitle();
						if (StringUtils.isNotEmpty(result.getMakeErrorMsg())) {
							process.setError(result.getMakeErrorMsg());
							errorProcessList.add(process);
						}
						process.setType(MakeType.LIST_CONTENT_INCREMENT);
						String title = String.format("%s/%s [%s] %s <br/>&nbsp;&nbsp;&nbsp;&nbsp; %s/%s [%s] %s", i + 1, split.length,
								column.getId(), column.getTitle(), j, count, id, name);
						process.setTitle(title);
						process.setPercent(percent);
						sendMesage(new JSONSerializer().serialize(process));
					}
				}
			}
		}

		process = new MakeProcess();
		process.setWarn("开始生成列表页");
		sendMesage(new JSONSerializer().serialize(process));

		List<Column> columns =produceFileService.findColumnByIds(columnIds);
		CompletionService<MakeFileResult> completionService = makeFileService.makeListFile(columns,null);
		int size=columns.size();
		makeCount+=size;
		for (int i = 1; i <= size; i++) {
			MakeFileResult makeFileResult = null;
			int percent = 0;
			try {
				Future<MakeFileResult> future = completionService.take();
				makeFileResult = future.get();
				if(makeFileResult.getMakeResult().equals(MakeFileResult.MAKE_RESULT.SUCCESS)){
					successCount++;
				}else{
					failCount++;
				}
				if(makeFileResult.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
					errorCount++;
				}
				percent = Math.round((tempCount+i) * 1f /totalCount * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				process = new MakeProcess();
				process.setType(MakeType.LIST_CONTENT_INCREMENT);
				String title = String.format("%s/%s [%s] %s", i, size, makeFileResult.getId(), makeFileResult.getTitle());
				process.setTitle(title);
				process.setPercent(percent);
				if (StringUtils.isNotEmpty(makeFileResult.getMakeErrorMsg())) {
					process.setError(makeFileResult.getMakeErrorMsg());
					errorProcessList.add(process);
				}
				if(size==i){
					process.setIsComplete(true);
					if(percent!=100){
						process.setPercent(100);
					}
				}
				sendMesage(new JSONSerializer().serialize(process));
			}
		}
		String produceResult=MAKE_RESULT.SUCCESS.name();
		if(errorCount!=0&&errorCount<makeCount){
			produceResult=MAKE_RESULT.PART_SUCCESS.name();
		}else if(makeCount!=0&&errorCount==makeCount){
			produceResult=MAKE_RESULT.FAIL.name();
		}
		String detail = "批量生成-刷新内容页和列表页：栏目=%s 耗时=%s秒 错误=%s个";
		detail = String.format(detail, columnIds, (System.currentTimeMillis() - b) / 1000, errorProcessList.size());
		String produceType=MakeFileTask.MakeEventType.CONTENT.getKey()+","+MakeFileTask.MakeEventType.LISTS.getKey();
		saveProduceLog(produceType, detail, errorProcessList, makeCount, successCount, failCount, produceResult);
	}

	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.LIST_CONTENT_INCREMENT.equals(makeType);
	}

}
