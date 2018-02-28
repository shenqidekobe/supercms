package com.dw.suppercms.application.manu.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.manu.ManuscriptOrganService;
import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.dw.suppercms.domain.manu.ManuscriptOrganRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

@ApplicationService
public class ManuscriptOrganServiceImpl implements ManuscriptOrganService {
	
	@Resource
	private ManuscriptOrganRepository manuscriptOrganRepository;

	@Override
	public ManuscriptOrgan findOrganById(Long organId) {
		assertNotNull("organId must not be null", organId);
		return manuscriptOrganRepository.find(organId);
	}

	@Override
	public ManuscriptOrgan findPlatformOrganById(String platformOrganId) {
		assertNotNull("platformOrganId must not be null", platformOrganId);
		List<ManuscriptOrgan> list=manuscriptOrganRepository.search(new Search().addFilterEqual("platformOrganId", platformOrganId));
		return list.size()==0?null:list.get(0);
	}

	@Override
	public Boolean validatePlatformOrganId(String platformOrganId) {
		return findPlatformOrganById(platformOrganId)==null?true:false;
	}

	@Override
	public ManuscriptOrgan create(ManuscriptOrgan manuscriptOrgan) {
		this.manuscriptOrganRepository.save(manuscriptOrgan);
		return manuscriptOrgan;
	}

	@Override
	public ManuscriptOrgan update(Long id, ManuscriptOrgan newManuscriptOrgan) {
		ManuscriptOrgan obj=this.findOrganById(id);
		BeanUtils.copyProperties(newManuscriptOrgan, obj,new String[]{"alreadyManuCount"});
		this.manuscriptOrganRepository.save(obj);
		return obj;
	}

	@Override
	public Long delete(Long id) {
		ManuscriptOrgan organ=this.findOrganById(id);
		if(organ.getMembers().size()>0){
			throw new BusinessException(String.format("当前单位：%s 存在下级用户，不能直接删除",organ.getOrganName()));
		}
		this.manuscriptOrganRepository.remove(organ);
	    return id;
	}

	@Override
	public List<ManuscriptOrgan> all() {
		return this.manuscriptOrganRepository.findAll();
	}

	@Override
	public SearchResult<ManuscriptOrgan> paginateAll(int startIndex,
			int maxResults) {
		Search search = new Search(ManuscriptOrgan.class);
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("createTime", true)));
		return manuscriptOrganRepository.searchAndCount(search);
	}

}
