package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;
import com.dw.suppercms.infrastructure.web.websocket.dto.MakeProcess;
import com.dw.suppercms.infrastructure.web.websocket.listener.BatchMakeFileScheduleListener;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;
import com.dw.suppercms.produce.MakeFileResult;
import com.dw.suppercms.produce.MakeFileResult.MAKE_ERROR_CODE;
import com.dw.suppercms.produce.MakeFileResult.MAKE_RESULT;
import com.dw.suppercms.produce.listener.event.MakeFileTask;

import flexjson.JSONSerializer;

/**
 * 批量更新站点首页
 * */
@Service
public class BatchMakeFileIndexProcess extends BatchMakeFileScheduleListener {

	@Override
	protected void execute(BatchTask task) {
		Long b = System.currentTimeMillis();
		String siteIds=task.getParams();
		List<MakeProcess> errorProcessList = new ArrayList<MakeProcess>();
		log.info("批量生成-刷新首页：id=" + siteIds);
		List<Site> sites = produceFileService.findSiteByIds(siteIds);
		CompletionService<MakeFileResult> completionService = makeFileService.makeIndexFile(sites);
		int size= sites.size();
		int makeCount=size;
		int successCount=0;
		int failCount=0;
		int errorCount=0;
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
				percent = Math.round(i * 1f / size * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				MakeProcess process = new MakeProcess();
				process.setType(MakeType.INDEX);
				String title = String.format("%s/%s [%s] %s", i, size, makeFileResult.getId(), makeFileResult.getTitle());
				process.setTitle(title);
				process.setPercent(percent);
				if (StringUtils.isNotEmpty(makeFileResult.getMakeErrorMsg())) {
					process.setError(makeFileResult.getMakeErrorMsg());
					errorProcessList.add(process);
				}
				if(size==i){
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
		String detail = "批量生成-刷新首页：首页=%s 耗时=%s秒 错误=%s个";
		detail = String.format(detail, siteIds, (System.currentTimeMillis() - b) / 1000, errorProcessList.size());
		saveProduceLog(MakeFileTask.MakeEventType.INDEX.getKey(), detail, errorProcessList, makeCount, successCount, failCount, produceResult);
	}

	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.INDEX.equals(makeType);
	}

}
