package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.dw.suppercms.produce.rule.VariableDefine;
import com.googlecode.genericdao.search.SearchResult;

import flexjson.JSONSerializer;

/**
 * 批量生成栏目内容页
 * */
@Service
public class BatchMakeFileContentPorcess extends BatchMakeFileScheduleListener{

	@Override
	protected void execute(BatchTask task) {
		Long b = System.currentTimeMillis();
		String message=task.getParams();
		List<MakeProcess> errorProcessList = new ArrayList<MakeProcess>();
		log.info("自定义生成-生成内容页：" + message);
		String[] split = message.split("#");
		String columnIds = split[1];
		String flag = VariableDefine.map_params_query_produceall;
		String begin=null,end=null;
		if(split.length>2){
			flag = split[2].equals("true") ? VariableDefine.map_params_query_produceall : VariableDefine.map_params_query_producenot;
			begin = split[3].equals("-1") ? null : split[3];
			end = split[4].equals("-1") ? null : split[4];
		}

		MakeProcess process = null;
		
		int makeCount=0;
		int successCount=0;
		int failCount=0;
		int errorCount=0;

		String[] strColumnIds = columnIds.split(",");
		int length=strColumnIds.length;
		for (int i = 0; i < length; i++) {
			Long columnId=Long.valueOf(strColumnIds[i]);
			Column column = produceFileService.findColumnById(columnId);
			Pager pager = new Pager();
			pager.setPageSize(50);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Map<String,Object> params=new HashMap<>();
			params.put(VariableDefine.map_params_query_produce_key, flag);
			
			Date startTime=null,endTime=null;
			try {
				if(StringUtils.isNotEmpty(begin)){
					startTime=sdf.parse(begin);
				}if(StringUtils.isNotEmpty(end)){
					endTime=sdf.parse(end);
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			SearchResult<DataRecord> sr=produceFileService.findDataRecordListUseDirective(params, Arrays.asList(columnId), startTime, endTime, pager);
			int rowCount=sr.getTotalCount();
			pager.calcPageCount((long) rowCount);
			List<DataRecord> list=sr.getResult();
			if (rowCount != 0) {
				makeCount+=rowCount;
				CompletionService<MakeFileResult> completionService = makeFileService.makeContentFileGoCompletion(column, list);
				for (int j = 1; j <= rowCount; j++) {
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
						percent = Math.round((j) * 1f / rowCount * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						process = new MakeProcess();
						String id = result.getDataId();
						String name = result.getDataTitle();
						if (StringUtils.isNotEmpty(result.getMakeErrorMsg())) {
							process.setError(result.getMakeErrorMsg());
							errorProcessList.add(process);
						}
						process.setType(MakeType.CONTENT);
						String title = String.format("[%s/%s] [id=%s] %s <br/>&nbsp;&nbsp;&nbsp;&nbsp; [%s/%s] [id=%s] %s", i + 1, strColumnIds.length,
								column.getId(), column.getTitle(), j, rowCount, id, name);
						process.setTitle(title);
						process.setPercent(percent);
						if(length==(i+1)&&j==rowCount){
							process.setIsComplete(true);
						}
						sendMesage(new JSONSerializer().serialize(process));
					}
				}
			} else {
				process = new MakeProcess();
				String title = String.format("%s/%s [%s] %s %s", i + 1, length, column.getId(), column.getTitle(), "没有内容页生成");
				process.setTitle(title);
				process.setPercent(100);
				sendMesage(new JSONSerializer().serialize(process));
			}

		}
		String produceResult=MAKE_RESULT.SUCCESS.name();
		if(errorCount!=0&&errorCount<makeCount){
			produceResult=MAKE_RESULT.PART_SUCCESS.name();
		}else if(makeCount!=0&&errorCount==makeCount){
			produceResult=MAKE_RESULT.FAIL.name();
		}
		String detail = "自定义生成-生成内容页：栏目=%s 耗时=%s秒 错误=%s个";
		detail = String.format(detail, columnIds, (System.currentTimeMillis() - b) / 1000, errorProcessList.size());
		saveProduceLog(MakeFileTask.MakeEventType.CONTENT.getKey(), detail, errorProcessList, makeCount, successCount, failCount, produceResult);
	}

	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.CONTENT.equals(makeType);
	}

}
