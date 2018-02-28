package com.dw.suppercms.produce.listener.make;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.application.system.SysParamService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_KEY;
import com.dw.suppercms.produce.MakeFileService;
import com.dw.suppercms.produce.execption.MakeListExecption;
import com.dw.suppercms.produce.listener.MakeTemplateFileProcessorListener;
import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.rule.MakeFileCommon;
import com.dw.suppercms.produce.rule.VariableDefine;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * 生成列表页文件任务
 * */
@Service
public class MakeListFileTask extends MakeTemplateFileProcessorListener {
	
	@Resource
	private MakeFileCommon makeFileCommon;
	
	@Resource
	private MakeFileService makeFileService;
	
	@Resource
	private ProduceFileService produceFileService;
	
	@Resource
	private SysParamService sysParamService;

	@Override
	protected void makeFile(MakeFileTask task) {
		try {
			Column column=task.getColumn();
			if(column.getMakeListState()){
				return;//已经生成的列表不重新生成
			}
			String listPath=column.getFileDiskpath(1);
			logger.info(String.format("执行栏目【列表页】生成任务 栏目id=%s 栏目名称=%s 生成地址=%s",task.getFileId(), task.getSourceName(),listPath));
			SimpleHash simpleHash = makeFileCommon.bulidBasicSimpleHash();
			simpleHash.put(VariableDefine.variable_cid, task.getFileId());
			simpleHash.put(VariableDefine.variable_sid, task.getWebId());
			simpleHash.put(VariableDefine.variable_cname, task.getSourceName());
			simpleHash.put(VariableDefine.variable_pid, task.getParentId());
			simpleHash.put(VariableDefine.variable_clink, column.getFileWebpath(1));
			
			//生成第一页
			File file = makeFileCommon.createFile(listPath);
			
			Writer output = new StringWriter();
			Template template = makeFileCommon.getConfiguration().getTemplate(VariableDefine.template_prefix_templateinfo + task.getTemplateId());
			template.process(simpleHash, output);
			FileUtils.write(file, output.toString());
			
			//判断是否需要生成多页
			Object pagethNumObj=template.getCustomAttribute(task.getFileId() +VariableDefine.pading_pagethNum);
			if ( pagethNumObj!= null) {
				int pagethNum = Integer.parseInt(pagethNumObj.toString());
				for (int i = 2; i <= pagethNum; i++) {
					simpleHash.put(VariableDefine.pading_pageth, i);
					output = new StringWriter();
					file = makeFileCommon.createFile(column.getFileDiskpath(i));
					template.process(simpleHash, output);
					FileUtils.write(file, output.toString());
				}
			}
			
			//更新栏目的生成列表状态为已生成
			this.produceFileService.modifyColumnListState(column.getId(), true);
			//生成当前月的历史数据
			new Thread(new Runnable() {
				public void run() {
					String isProduceListHistory="true";//默认生成
					String paramVal=sysParamService.findSysParamValByCodeAndKey(PARAM_CODE.produce, PARAM_KEY.isProduceHistory.toString());
					if(StringUtils.isNotEmpty(paramVal)){
						isProduceListHistory=paramVal;
					}
					if("true".equals(isProduceListHistory)){
						makeFileService.makeHistoryListFileTask(column, 0);
					}
				}
			}).start();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MakeListExecption(e.toString() + ":" +e.getMessage());
		}

	}

	@Override
	public boolean supportsMakeType(MakeEventType makeEventType) {
		return MakeEventType.LISTS.equals(makeEventType);
	}

}
