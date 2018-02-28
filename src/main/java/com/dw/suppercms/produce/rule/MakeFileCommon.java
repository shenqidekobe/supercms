package com.dw.suppercms.produce.rule;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.suppercms.produce.directive.AsignDirective;
import com.dw.suppercms.produce.directive.HitsTopDirective;
import com.dw.suppercms.produce.directive.PageDirective;
import com.dw.suppercms.produce.directive.PlugInDirective;
import com.dw.suppercms.produce.directive.PraiseTopDirective;
import com.dw.suppercms.produce.directive.RecordDirective;
import com.dw.suppercms.produce.directive.SnippetDirective;
import com.dw.suppercms.produce.directive.StatDirective;
import com.dw.suppercms.produce.directive.VideoDirective;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;

/**
 * 生成文件公用方法集合,指令、方法集中
 * */
@Service
public class MakeFileCommon {
	
	@Resource(name="configurationRule")
	private Configuration configurationRule;
	@Resource
	private AsignDirective asignDirective;
	@Resource
	private HitsTopDirective hitsTopDirective;
	@Resource
	private PageDirective pageDirective;
	@Resource
	private PlugInDirective plugInDirective;
	@Resource
	private PraiseTopDirective praiseTopDirective;
	@Resource
	private RecordDirective recordDirective;
	@Resource
	private SnippetDirective snippetDirective;
	@Resource
	private VideoDirective videoDirective;
	@Resource
	private StatDirective statDirective;
	
	/**
	 * 构建常用基本的SimpleHash对象
	 * */
	public SimpleHash bulidBasicSimpleHash(){
		SimpleHash simpleHash=new SimpleHash(getConfiguration().getObjectWrapper());
		simpleHash.put(VariableDefine.directive_asign, asignDirective);
		simpleHash.put(VariableDefine.directive_cons, snippetDirective);
		simpleHash.put(VariableDefine.directive_hitsTop, hitsTopDirective);
		simpleHash.put(VariableDefine.directive_page, pageDirective);
		simpleHash.put(VariableDefine.directive_plugin, plugInDirective);
		simpleHash.put(VariableDefine.directive_praiseTop, praiseTopDirective);
		simpleHash.put(VariableDefine.directive_rec, recordDirective);
		simpleHash.put(VariableDefine.directive_stat, statDirective);
		simpleHash.put(VariableDefine.directive_video, videoDirective);
		return simpleHash;
	}
	
	/**
	 * 获取freemarker的核心配置
	 * */
	public Configuration getConfiguration(){
		try {
			return configurationRule;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 创建一个文件，如果文件的上级目录不存在就创建一个文件夹
	 * */
	public File createFile(String filePath){
		File file=new File(filePath);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		return file;
	}

}
