package com.dw.suppercms.application.plugin;

import java.util.List;

import com.dw.suppercms.domain.plugin.PlugInInfo;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 插件管理服务接口
 * */
public interface PlugInManageService {
	
	
	/**
	 * 检索插件信息
	 * */
	PlugInInfo findPlugInById(Long plugId);
	
	/**
	 * 获取插件信息按标记
	 * */
	PlugInInfo findPlugInByTag(String tag);
	

	/**
	 * 验证插件标记是否唯一
	 * 
	 * @param filedName PlugInInfo's dirName
	 * @return true if valid, false if invalid
	 */
	public Boolean validateFiledName(String filedName);

	/**
	 * create a new PlugInInfo
	 * 
	 * @param PlugInInfo holds the new PlugInInfo state
	 * @return the saved PlugInInfo
	 */
	PlugInInfo create(PlugInInfo PlugInInfo);


	/**
	 * update a PlugInInfo
	 * 
	 * @param id the id of the updating PlugInInfo
	 * @param newPlugInInfo holds the new PlugInInfo state
	 * @return the updated PlugInInfo
	 */
	PlugInInfo update(Long id, PlugInInfo newPlugInInfo);

	/**
	 * delete a PlugInInfo
	 * 
	 * @param id the id of the deleting PlugInInfo
	 * @return the deleted PlugInInfo's id
	 */
	Long delete(Long id);

	/**
	 * retrieve all PlugInInfos
	 * 
	 * @return all PlugInInfos
	 */
	List<PlugInInfo> all();

	/**
	 * retrieve all PlugInInfos with pagination
	 * 
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return pagination PlugInInfos
	 */
	SearchResult<PlugInInfo> paginateAll(int startIndex, int maxResults);
	

}
