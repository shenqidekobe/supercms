package com.dw.suppercms.produce.listener.make;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.produce.execption.MakeCustomExecption;
import com.dw.suppercms.produce.listener.MakeTemplateFileProcessorListener;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.MakeFileCommon;
import com.dw.suppercms.produce.rule.VariableDefine;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * 生成自定义文件任务
 * */
@Service
public class MakeCustomFileTask extends MakeTemplateFileProcessorListener{
	
	@Resource
	private MakeFileCommon makeFileCommon;

	@Override
	protected void makeFile(MakeFileTask task) {
		logger.info(String.format("执行【自定义页】生成任务 id=%s 页面名称=%s 生成地址=%s", task.getFileId(), task.getSourceName(),task.getFilePath()));
	    try {
			SimpleHash simpleHash=makeFileCommon.bulidBasicSimpleHash();
			
			File file=makeFileCommon.createFile(task.getFilePath());
			
			Writer output=new StringWriter();
			//如果自定义页有自己的模版则采用模版、反之采用自己的code
			Long customTemplateId=task.getTemplateId();
			Template template=null;
			if(customTemplateId==null){
				template=makeFileCommon.getConfiguration().getTemplate(VariableDefine.template_prefix_custominfo+task.getFileId());
			}else{
				template=makeFileCommon.getConfiguration().getTemplate(VariableDefine.template_prefix_templateinfo+customTemplateId);
			}
			template.process(simpleHash, output);
			FileUtils.write(file, output.toString());
	    } catch (Exception e) {
	    	e.printStackTrace();
			throw new MakeCustomExecption(e.toString() + ":" +e.getMessage());
		}
	}

	@Override
	public boolean supportsMakeType(MakeEventType makeEventType) {
		return MakeEventType.CUSTOMS.equals(makeEventType);
	}

}
