package com.dw.suppercms.application.system;

import java.util.List;

import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.googlecode.genericdao.search.SearchResult;

public interface SysSortService {
	
	/**
	 * 创建分类
	 * @param parentId
	 * @param sortType
	 * @param sortName
	 * */
	boolean createSysSort(Long parentId,String sortType,String sortName);
	
	/**
	 * 编辑分类
	 * */
	boolean modifySysSort(Long id,SysSortInfo sort);
	
	/**
	 * 删除分类
	 * */
	boolean removeSysSort(Long id);
	
	/**
	 * 根据类型获取分类集合
	 * */
	List<SysSortInfo> findRootSortByType(String type);
	
	/**
	 * 检索分类按ID
	 * */
	SysSortInfo findSortById(Long sortId);
	
	/**
	 * 验证分类是否存在
	 * */
	boolean findSortNameIsExists(String sortName,String sortType);
	
	/**
	 * 按类型获取分类集合
	 * */
	List<SysSortInfo> findSortsByType(SORT_TYPE type);
	
	/**
	 * 按类型检索分类列表
	 * */
	SearchResult<SysSortInfo> findSortList(SORT_TYPE type,int startIndex,int maxResults);
	
}
