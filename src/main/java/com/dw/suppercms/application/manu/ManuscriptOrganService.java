package com.dw.suppercms.application.manu;

import java.util.List;

import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 投稿单位接口
 * */
public interface ManuscriptOrganService {
	
	/**
	 * 检索投稿单位信息
	 * */
	ManuscriptOrgan findOrganById(Long organId);
	
	/**
	 * 根据平台单位ID获取投稿单位
	 * */
	ManuscriptOrgan findPlatformOrganById(String platformOrganId);
	
	/**
	 * 验证平台单位ID是否唯一
	 * 
	 * @param platformOrganId
	 * @return boolean
	 */
	public Boolean validatePlatformOrganId(String platformOrganId);

	/**
	 * 创建一个新的单位
	 * 
	 */
	ManuscriptOrgan create(ManuscriptOrgan manuscriptOrgan);


	/**
	 * 修改一个旧的单位
	 * 
	 */
	ManuscriptOrgan update(Long id, ManuscriptOrgan newManuscriptOrgan);

	/**
	 * 删除一个单位{需要验证该单位下面是否有用户}
	 * 
	 */
	Long delete(Long id);

	/**
	 * 获取所有的单位信息
	 * */
	List<ManuscriptOrgan> all();

	/**
	 * 检索单位列表
	 * 
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return pagination ManuscriptOrgans
	 */
	SearchResult<ManuscriptOrgan> paginateAll(int startIndex, int maxResults);

}
