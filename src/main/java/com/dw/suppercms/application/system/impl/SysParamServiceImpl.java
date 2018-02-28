package com.dw.suppercms.application.system.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.system.SysParamService;
import com.dw.suppercms.domain.system.SysParamInfo;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;
import com.dw.suppercms.domain.system.SysParamRepository;
import com.googlecode.genericdao.search.Search;

/**
 * 系统参数服务实现类
 * */
@ApplicationService
public class SysParamServiceImpl implements SysParamService{
	
	@Resource
	private SysParamRepository sysParamRepository;

	@Override
	public SysParamInfo createSysParam(SysParamInfo obj) {
		assertNotNull(obj);
		sysParamRepository.save(obj);
		return obj;
	}

	@Override
	public SysParamInfo modifySysParam(Long id, SysParamInfo obj) {
		assertNotNull(obj);
		SysParamInfo pojo=this.findSysParamById(id);
		BeanUtils.copyProperties(obj, pojo);
		this.sysParamRepository.save(pojo);
		return pojo;
	}

	@Override
	public void removeSysParam(Long id) {
		assertNotNull(id);
		this.sysParamRepository.removeById(id);
	}

	@Override
	public boolean validateParamKey(PARAM_CODE paramCode, String paramKey) {
		assertNotNull(paramCode);
		assertNotNull(paramKey);
		return this.sysParamRepository.search(new Search().addFilterEqual("paramCode", paramCode).addFilterEqual("paramKey", paramKey)).size()==0?true:false;
	}

	@Override
	public SysParamInfo findSysParamById(Long id) {
		assertNotNull(id);
		return this.sysParamRepository.find(id);
	}

	@Override
	public List<SysParamInfo> findSysParamListByCode(PARAM_CODE paramCode) {
		return this.sysParamRepository.search(new Search().addFilterEqual("paramCode", paramCode));
	}

	@Override
	public SysParamInfo findSysParamByCodeAndKey(PARAM_CODE paramCode, String paramKey) {
		List<SysParamInfo> list=this.sysParamRepository.search(new Search().addFilterEqual("paramCode", paramCode).addFilterEqual("paramKey", paramKey));
		if(!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public String findSysParamValByCodeAndKey(PARAM_CODE paramCode,String paramKey){
		SysParamInfo param=findSysParamByCodeAndKey(paramCode, paramKey);
		if(param!=null){
			return param.getParamVal();
		}
		return null;
	}
	
	@Override
	public List<SysParamInfo> all(){
		return this.sysParamRepository.findAll();
	}

}
