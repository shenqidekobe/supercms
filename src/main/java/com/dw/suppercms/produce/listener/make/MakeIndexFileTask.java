package com.dw.suppercms.produce.listener.make;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.produce.execption.MakeIndexExecption;
import com.dw.suppercms.produce.listener.MakeTemplateFileProcessorListener;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.MakeFileCommon;
import com.dw.suppercms.produce.rule.VariableDefine;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * 生成首页文件任务
 * */
@Service
public class MakeIndexFileTask extends MakeTemplateFileProcessorListener{
	
	@Resource
	private MakeFileCommon makeFileCommon;
	
	@Override
	protected void makeFile(MakeFileTask task) {
		logger.info(String.format("执行【首页】生成任务站点 id=%s 首页名称=%s 生成地址=%s", task.getFileId(), task.getSourceName(),task.getFilePath()));
		try {
		    SimpleHash simpleHash=makeFileCommon.bulidBasicSimpleHash();
		    simpleHash.put(VariableDefine.variable_sid, task.getFileId());
		    
		    
		    File file=makeFileCommon.createFile(task.getFilePath());
		    Writer output=new StringWriter();
		    Template template=makeFileCommon.getConfiguration().getTemplate(VariableDefine.template_prefix_templateinfo+task.getTemplateId());		   
		    template.process(simpleHash, output);
		    FileUtils.write(file, output.toString());
		    
		    //将生成目录的文件copy到发布目录下面--默认不发布
		    /*Site site=task.getSite();
		    String src = site.getDirDiskpath();
			String desc = site.getPubDiskpath();
			new File(desc).mkdirs();
			CmsFileUtils.sysncDirs(src, desc);*/
		} catch (Exception e) {
			e.printStackTrace();
			throw new MakeIndexExecption(e.toString() + ":" +e.getMessage());
		}
	}

	@Override
	public boolean supportsMakeType(MakeEventType makeEventType) {
		return MakeEventType.INDEX.equals(makeEventType);
	}

}
