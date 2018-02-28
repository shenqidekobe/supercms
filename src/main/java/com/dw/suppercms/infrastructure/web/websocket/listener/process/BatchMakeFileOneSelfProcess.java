package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

import flexjson.JSONSerializer;

/**
 * 批量更新自己创建的信息
 * */
@Service
public class BatchMakeFileOneSelfProcess extends BatchMakeFileScheduleListener{

	@Override
	protected void execute(BatchTask task) {
		Long b = System.currentTimeMillis();
		String userId=task.getParams();
		List<MakeProcess> errorProcessList = new ArrayList<MakeProcess>();
		MakeProcess process = null;
		String info = null;
		List<Column> columns = null;
		if (userId.equals("-1")) {
			info = "快捷生成-刷新新增最终页和列表页";
			columns = produceFileService.findAllColumns();
		} else {
			info = "快捷生成-刷新自己的所有信息";
			columns = produceFileService.findColumnByUser(Long.valueOf(userId));

			if(columns.isEmpty()){
				process = new MakeProcess();
				process.setTitle("没有你要生成的信息");
				process.setPercent(100);
				sendMesage(new JSONSerializer().serialize(process));
				return;
			}
		}
		log.info(info);
		Set<Column> columnsSet = new HashSet<Column>();
		//定义记录生成日志的变量
		int makeCount=0;
		int successCount=0;
		int failCount=0;
		
		//定义输出进度的变量
		int columnSize=columns.size();
		int defaultEvery=10000;
		int totalCount=columnSize*defaultEvery+columnSize;
		int tempCount=0;
		boolean flag=true;
		int errorCount=0;
		for (int i = 0; i < columnSize; i++) {
			flag=true;
			Column column = columns.get(i);
			Pager pager = new Pager();
			pager.setPageSize(50);
			
			Map<String,Object> params=new HashMap<>();
			params.put(VariableDefine.map_params_query_produce_key, VariableDefine.map_params_query_producenot);//查询未生成的数据
			SearchResult<DataRecord> sr=produceFileService.findDataRecordListUseDirective(params, Arrays.asList(column.getId()), null, null, pager);
			
			int rowCount=sr.getTotalCount();
			pager.calcPageCount((long) rowCount);
			List<DataRecord> list=sr.getResult();
			if (rowCount != 0) {
				makeCount+=rowCount;
				CompletionService<MakeFileResult> completionService = makeFileService.makeContentFileGoCompletion(column,list);
				int size=list.size();
				for (int j = 1; j <=size; j++) {
					tempCount++;
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
						if(size<=defaultEvery){
							if(flag){
								tempCount+=defaultEvery-size;
								flag=false;
							}
						}else{
							if(flag){
								totalCount+=size-defaultEvery;
								flag=false;
							}
						}
						percent = Math.round((tempCount) * 1f / totalCount * 100);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} finally {
						columnsSet.add(column);
						process = new MakeProcess();
						String id = result.getDataId();
						String name = result.getDataTitle();
						if (StringUtils.isNotEmpty(result.getMakeErrorMsg())) {
							process.setError(result.getMakeErrorMsg());
							errorProcessList.add(process);
						}
						process.setType(MakeType.SELF_INCREMENT);
						String title = String.format("%s/%s [%s] %s <br/>&nbsp;&nbsp;&nbsp;&nbsp; %s/%s [%s] %s", i+1, columnSize,
								column.getId(), column.getTitle(), j, rowCount, id, name);
						process.setTitle(title);
						process.setPercent(percent);
						sendMesage(new JSONSerializer().serialize(process));
					}
				}
			} else {
				process = new MakeProcess();
				String title = String.format("%s/%s [%s] %s %s", i + 1, columnSize, column.getId(), column.getTitle(), "没有内容页生成");
				process.setTitle(title);
				process.setPercent(100);
				sendMesage(new JSONSerializer().serialize(process));
			}
		}

		int columnsSetSize=columnsSet.size();
		makeCount+=columnsSetSize;
		if (columnsSetSize!= 0) {
			CompletionService<MakeFileResult> completionService = makeFileService.makeListFile(new ArrayList<Column>(columnsSet),null);
			for (int i = 1; i <= columnsSetSize; i++) {
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
					percent = Math.round((i+tempCount) * 1f / totalCount * 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} finally {
					process = new MakeProcess();
					process.setType(MakeType.SELF_INCREMENT);
					String title = String.format("%s/%s [%s] %s", i, columnsSetSize, makeFileResult.getId(), makeFileResult.getTitle());
					process.setTitle(title);
					process.setPercent(percent);
					if (StringUtils.isNotEmpty(makeFileResult.getMakeErrorMsg())) {
						process.setError(makeFileResult.getMakeErrorMsg());
						errorProcessList.add(process);
					}
					if(columnsSetSize==i){
						process.setIsComplete(true);
						if(percent!=100){
							if(percent!=100){
								process.setPercent(100);
							}
						}
					}
					sendMesage(new JSONSerializer().serialize(process));
				}
			}
		} else {
			process = new MakeProcess();
			process.setWarn("没有列表页生成");
			sendMesage(new JSONSerializer().serialize(process));
		}
		String produceResult=MAKE_RESULT.SUCCESS.name();
		if(errorCount!=0&&errorCount<makeCount){
			produceResult=MAKE_RESULT.PART_SUCCESS.name();
		}else if(makeCount!=0&&errorCount==makeCount){
			produceResult=MAKE_RESULT.FAIL.name();
		}

		String detail = userId.equals("-1") ? "快捷生成-刷新新增最终页和列表页： 耗时=%s秒 错误=%s个" : "快捷生成-刷新自己的所有信息： 耗时=%s秒 错误=%s个";
		detail = String.format(detail, (System.currentTimeMillis() - b) / 1000, errorProcessList.size());
		String produceType=MakeFileTask.MakeEventType.CONTENT.getKey()+","+MakeFileTask.MakeEventType.LISTS.getKey();
		saveProduceLog(produceType, detail, errorProcessList, makeCount, successCount, failCount, produceResult);
	}

	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.SELF_INCREMENT.equals(makeType);
	}

}
