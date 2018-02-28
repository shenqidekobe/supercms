package com.dw.suppercms.domain.plugin;

import java.util.List;

import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface PlugInRepository extends GenericDAO<PlugInInfo,Long>{
	
	/** 根据名字查找插件对象*/
	public PlugInInfo queryByName(String name);
	
	/**
	 * 按用户检索插件
	 * */
	public Long queryPlugInCount();
	
	/**
	 * 插件列表查询
	 * */
	public List<PlugInInfo> queryPlugInList(Pager pager);

}
