package com.dw.suppercms.produce.listener.make;

import java.io.File;
import java.io.Writer;
import java.sql.Timestamp;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.index.IndexSearchService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.execption.MakeContentExecption;
import com.dw.suppercms.produce.listener.MakeTemplateFileProcessorListener;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.FreemarkerUtils;
import com.dw.suppercms.produce.rule.MakeFileCommon;

import freemarker.template.SimpleHash;

/**
 * 生成内容页文件任务
 * */
@Service
public class MakeContentFileTask extends MakeTemplateFileProcessorListener {
	
	@Resource
	private ProduceFileService produceFileService;
	
	@Resource
	private MakeFileCommon makeFileCommon;
	
	@Resource
	private IndexSearchService indexSearchService;

	@Override
	protected void makeFile(MakeFileTask task) {
		try {
			DataRecord dataRecord=task.getDataRecord();
			String contentPath=task.getColumn().getContentFilePath(dataRecord.getId(), new Timestamp(dataRecord.getTime().getTime()));
			logger.info(String.format("执行【最终页】生成任务栏目id=%s 数据id=%s 栏目名称=%s 生成地址=%s", task.getFileId(),task.getDataRecord().getId(),task.getSourceName(),contentPath));
			SimpleHash simpleHash = makeFileCommon.bulidBasicSimpleHash();
			
			File file=makeFileCommon.createFile(contentPath);
			
			//解析最终页模版
			Writer output=FreemarkerUtils.parseContentTemplate(task, simpleHash, makeFileCommon.getConfiguration());
			FileUtils.write(file, output.toString());
			
			//更新栏目数据关系的生成状态
			Long columnId=task.getColumn().getId();
			this.produceFileService.modifyColumnDataProduceState(columnId,Long.parseLong(dataRecord.getId()), dataRecord.getDataSourceId(), true);
			
			//更新栏目的生成列表状态为需要重新生成
			try {
				this.produceFileService.modifyColumnListState(columnId, false);
			} catch (Exception e) {}
			
			//推送数据到索引服务器
			new Thread(new Runnable() {
				public void run() {
					indexSearchService.pushData(dataRecord.getDataSourceId(),Long.parseLong(dataRecord.getId()));
				}
			}).start();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MakeContentExecption(e.toString() + ":" +e.getMessage());
		}
	}

	@Override
	public boolean supportsMakeType(MakeEventType makeEventType) {
		return MakeEventType.CONTENT.equals(makeEventType);
	}
	
	
}
