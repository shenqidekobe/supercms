package com.dw.suppercms.application.plugin.impl;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.framework.core.SpringHelper;
import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.plugin.PlugInManageService;
import com.dw.suppercms.domain.plugin.PlugInInfo;
import com.dw.suppercms.domain.plugin.PlugInRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

@ApplicationService
public class PlugInManageServiceImpl implements PlugInManageService {
	
	private static Logger logger=LoggerFactory.getLogger(PlugInManageServiceImpl.class);
	
	@Resource
	private PlugInRepository plugInRepository;


	@Override
	public PlugInInfo findPlugInById(Long plugId) {
		assertNotNull("id must not be null", plugId);
		return this.plugInRepository.find(plugId);
	}
	
	@Override
	public PlugInInfo findPlugInByTag(String tag){
	    return this.plugInRepository.queryByName(tag);	
	}
	
	@Override
	public Boolean validateFiledName(String filedName) {
		return findPlugInByTag(filedName) == null ? true : false;
	}

	@Override
	public PlugInInfo create(PlugInInfo plugInInfo) {
		plugInRepository.save(plugInInfo);
		//根据插件的标识创建对应的文件夹
		String path= SpringHelper.servletContext.getRealPath("/")+"resource/plugin/"+plugInInfo.getFieldName();
		logger.info("创建插件的对应的文件目录："+path);
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return plugInInfo;

	}

	@Override
	public PlugInInfo update(Long id, PlugInInfo newPlugInInfo) {
		PlugInInfo dbPlugInInfo = findPlugInById(id);
		if(!newPlugInInfo.getFieldName().equals(dbPlugInInfo.getFieldName())){
			//根据插件的标识创建对应的文件夹
			String path= SpringHelper.servletContext.getRealPath("/")+"resource/plugin/"+newPlugInInfo.getFieldName();
			logger.info("更新插件的对应的文件目录："+path);
			File file=new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
		}
		
		dbPlugInInfo.setPlugName(newPlugInInfo.getPlugName());
		dbPlugInInfo.setFieldName(newPlugInInfo.getFieldName());
		dbPlugInInfo.setPlugContent(newPlugInInfo.getPlugContent());
		dbPlugInInfo.setJsDefinition(newPlugInInfo.getJsDefinition());
		dbPlugInInfo.setJsDemo(newPlugInInfo.getJsDemo());
		dbPlugInInfo.setAttr1(newPlugInInfo.getAttr1());

		plugInRepository.save(dbPlugInInfo);

		return dbPlugInInfo;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		plugInRepository.removeById(id);

		return id;
	}

	@Override
	public List<PlugInInfo> all() {
		return plugInRepository.findAll();
	}

	@Override
	public SearchResult<PlugInInfo> paginateAll(int startIndex, int maxResults) {
		Search search = new Search(PlugInInfo.class);
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("createTime", true)));
		return plugInRepository.searchAndCount(search);
	}
	
}
