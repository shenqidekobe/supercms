package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;
import com.dw.suppercms.infrastructure.web.websocket.dto.MakeProcess;
import com.dw.suppercms.infrastructure.web.websocket.listener.BatchMakeFileScheduleListener;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_ERROR_CODE;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.rule.CmsFileUtils;

import flexjson.JSONSerializer;

/**
 * 批量生成某条内容的所有最终页(相关栏目的)
 * */
@Service
public class BatchMakeFileContentAlonePorcess extends BatchMakeFileScheduleListener{

	@Override
	protected void execute(BatchTask task) {
		Long b = System.currentTimeMillis();
		String message=task.getParams();
		List<MakeProcess> errorProcessList = new ArrayList<MakeProcess>();
		log.info("自定义生成-生成内容的所有栏目页：" + message);
		String[] split = message.split("#");
		String id = split[1];
		String datasourceId = split[2];
		
		//如果带有columnId就只生成当前栏目的内容、否则生成所有的关联栏目内容页
		List<Column> columns=new ArrayList<Column>();
		String columnId=null;
		if(split.length>3){
			columnId= split[3];
			columns.add(produceFileService.findColumnById(Long.parseLong(columnId)));
		}else{
			columns=produceFileService.findColumnByDatasourceId(Long.parseLong(datasourceId));
		}

		MakeProcess process = null;
		
		int makeCount=0;
		int successCount=0;
		int failCount=0;
		int errorCount=0;
		
		DataRecord dataRecord=this.produceFileService.findDataRecord(Long.parseLong(datasourceId), Long.parseLong(id));
		if(columns.isEmpty()){
			process = new MakeProcess();
			String title = String.format("%s/%s [%s] %s %s", 1, 0, id, dataRecord.getTitle(), "没有响应的栏目生成");
			process.setWarn(title);
			process.setPercent(100);
			sendMesage(new JSONSerializer().serialize(process));
		}
		CompletionService<MakeFileResult> completionService =this.makeFileService.makeContentFile(dataRecord, columns);
		int length=columns.size();
		for (int i = 0; i < length; i++) {
			MakeFileResult result = null;
			int percent = 0;
			try {
				Future<MakeFileResult> future = completionService.take();
				result = future.get();
				if(result.getMakeResult().equals(MakeFileResult.MAKE_RESULT.SUCCESS)){
					successCount++;
					//发布到public目录
					String src = result.getFilePath();
					String desc = src.replace(Module.MAKE_DIR, Module.PUB_DIR);
					new File(desc).mkdirs();
					CmsFileUtils.sysncDirs(src, desc);
				}else{
					failCount++;
				}
				if(result.getMakeErrorCode().equals(MAKE_ERROR_CODE.RUNTIME_EXCEPTION)){
					errorCount++;
				}
				percent = Math.round((i) * 1f / length * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				process = new MakeProcess();
				if (StringUtils.isNotEmpty(result.getMakeErrorMsg())) {
					process.setError(result.getMakeErrorMsg());
					errorProcessList.add(process);
				}
				process.setType(MakeType.CONTENT);
				String title = String.format("[%s/%s] [id=%s] %s <br/>&nbsp;&nbsp;&nbsp;&nbsp; [id=%s] %s", i + 1, length,
						result.getId(), result.getTitle(), id, dataRecord.getTitle());
				process.setTitle(title);
				process.setPercent(percent);
				if(length==(i+1)){
					process.setIsComplete(true);
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
		String detail = "自定义生成-生成内容的所有栏目页：数据ID=%s,数据源ID=%s 耗时=%s秒 错误=%s个";
		detail = String.format(detail, id,datasourceId,(System.currentTimeMillis() - b) / 1000, errorProcessList.size());
		saveProduceLog(MakeFileTask.MakeEventType.CONTENT.getKey(), detail, errorProcessList, makeCount, successCount, failCount, produceResult);
	}

	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.CONTENT_ALONE.equals(makeType);
	}

}
