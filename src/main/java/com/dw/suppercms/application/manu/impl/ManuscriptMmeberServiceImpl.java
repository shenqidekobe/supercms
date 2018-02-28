package com.dw.suppercms.application.manu.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.manu.ManuscriptMemberService;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_STATE;
import com.dw.suppercms.domain.manu.ManuscriptMember.MEMBER_TYPE;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;
import com.dw.suppercms.infrastructure.utils.MD5;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

@ApplicationService
public class ManuscriptMmeberServiceImpl implements ManuscriptMemberService{
	
	@Resource
	private ManuscriptMemberRepository manuscriptMemberRepository;

	@Override
	public ManuscriptMember findMemberById(Long id) {
		assertNotNull("id must not be null",id);
		return this.manuscriptMemberRepository.find(id);
	}
	

	@Override
	public ManuscriptMember findMemberByLoginNameAndPassword(String loginName,
			String password) {
		Search search=new Search();
		search.addFilterEqual("loginName", loginName);
		search.addFilterEqual("password", MD5.getMD5(password));
		return this.manuscriptMemberRepository.searchUnique(search);
	}

	@Override
	public Boolean validateLoginName(String loginName) {
		assertNotNull("loginName must not be null",loginName);
		return this.manuscriptMemberRepository.search(new Search().addFilterEqual("loginName", loginName)).size()==0?true:false;
	}

	@Override
	public Boolean validatePhone(String phone) {
		assertNotNull("phone must not be null",phone);
		return this.manuscriptMemberRepository.search(new Search().addFilterEqual("phone", phone)).size()==0?true:false;
	}

	@Override
	public Boolean validateEmail(String email) {
		assertNotNull("email must not be null",email);
		return this.manuscriptMemberRepository.search(new Search().addFilterEqual("email", email)).size()==0?true:false;
	}

	@Override
	public ManuscriptMember create(ManuscriptMember manuscriptMember) {
		assertNotNull("manuscriptMember must not be null",manuscriptMember);
		manuscriptMember.setPassword(MD5.getMD5(manuscriptMember.getPassword()));
		this.manuscriptMemberRepository.save(manuscriptMember);
		return manuscriptMember;
	}

	@Override
	public ManuscriptMember update(Long id, ManuscriptMember newManuscriptMember) {
		ManuscriptMember obj=findMemberById(id);
		String newPass=MD5.getMD5(newManuscriptMember.getPassword());
		if(!newPass.equals(obj.getPassword())){
			newManuscriptMember.setPassword(newPass);
		}
		BeanUtils.copyProperties(newManuscriptMember, obj);
		this.manuscriptMemberRepository.save(obj);
		return obj;
	}

	@Override
	public Long delete(Long id) {
		assertNotNull("id must not be null",id);
		this.manuscriptMemberRepository.removeById(id);
		return id;
	}

	@Override
	public List<ManuscriptMember> findMemberListByType(MEMBER_TYPE memberType,MEMBER_STATE state){
		Search search = new Search(ManuscriptMember.class);
		if (memberType!=null) {
			search.addFilterEqual("memberType", memberType);
		}
		if (state != null) {
			search.addFilterEqual("state", state);
		}
		return manuscriptMemberRepository.search(search);
	}

	@Override
	public List<ManuscriptMember> all(){
		return this.manuscriptMemberRepository.findAll();
	}
	
	@Override
	public SearchResult<ManuscriptMember> paginateAll(MEMBER_TYPE memberType,
			MEMBER_STATE state, int startIndex, int maxResults) {
		Search search = new Search(ManuscriptMember.class);
		if (memberType!=null) {
			search.addFilterEqual("memberType", memberType);
		}
		if (state != null) {
			search.addFilterEqual("state", state);
		}
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("createTime", true)));

		return manuscriptMemberRepository.searchAndCount(search);
	}


}
