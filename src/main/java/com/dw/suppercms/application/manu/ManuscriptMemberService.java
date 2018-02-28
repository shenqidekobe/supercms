package com.dw.suppercms.application.manu;

import java.util.List;

import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_STATE;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_TYPE;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 投稿用户接口
 * */
public interface ManuscriptMemberService {
	
	/**
	 * 检索投稿用户信息
	 * */
	ManuscriptMember findMemberById(Long id);
	
	/**
	 * 根据登录名和密码查询
	 * */
	ManuscriptMember findMemberByLoginNameAndPassword(String loginName,String password);
	
	/**
	 * 验证平登陆名称唯一性
	 * 
	 * @param loginName
	 * @return boolean
	 */
	public Boolean validateLoginName(String loginName);
	
	/**
	 * 验证电话号码唯一性
	 * 
	 * @param loginName
	 * @return boolean
	 */
	public Boolean validatePhone(String phone);
	
	/**
	 * 验证邮箱唯一性
	 * 
	 * @param loginName
	 * @return boolean
	 */
	public Boolean validateEmail(String email);

	/**
	 * 创建一个新的用户
	 * 
	 */
	ManuscriptMember create(ManuscriptMember manuscriptMember);


	/**
	 * 修改一个旧的用户
	 * 
	 */
	ManuscriptMember update(Long id, ManuscriptMember newManuscriptMember);

	/**
	 * 删除一个用户
	 * 
	 */
	Long delete(Long id);

	/**
	 * 获取所有的用户信息
	 * */
	List<ManuscriptMember> findMemberListByType(MEMBER_TYPE memberType,MEMBER_STATE state);
	
	/**
	 * 所有用户
	 * */
	List<ManuscriptMember> all();

	/**
	 * 检索用户列表
	 * 
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return pagination ManuscriptMembers
	 */
	SearchResult<ManuscriptMember> paginateAll(MEMBER_TYPE memberType,MEMBER_STATE state,int startIndex, int maxResults);

}
