package com.dw.suppercms.application.system;

import java.util.List;

import com.dw.suppercms.domain.system.SysParamInfo;
import com.dw.suppercms.domain.system.SysParamInfo.PARAM_CODE;

public interface SysParamService {
	
	/**
	 * 创建系统参数
	 * */
	SysParamInfo createSysParam(SysParamInfo obj);
	
	/**
	 * 编辑系统参数
	 * */
	SysParamInfo modifySysParam(Long id,SysParamInfo obj);
	
	/**
	 * 删除系统参数
	 * */
	void removeSysParam(Long id);
	
	/**
	 * 验证参数key是否存在
	 * */
	boolean validateParamKey(PARAM_CODE paramCode,String paramKey);
	
	/**
	 * 查询系统参数
	 * */
	SysParamInfo findSysParamById(Long id);
	
	/**
	 * 按code查询系统参数
	 * */
	List<SysParamInfo> findSysParamListByCode(PARAM_CODE paramCode);
	
	/**
	 * 按参数code和key查询
	 * */
	SysParamInfo findSysParamByCodeAndKey(PARAM_CODE code,String key);
	
	/**
	 * 获取参数值
	 * */
	String findSysParamValByCodeAndKey(PARAM_CODE code,String key);
	
	/**
	 * 所有参数
	 * */
	List<SysParamInfo> all();
	

}
