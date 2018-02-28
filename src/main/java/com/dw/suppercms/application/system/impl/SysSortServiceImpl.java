package com.dw.suppercms.application.system.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 分类服务
 * */
@ApplicationService
public class SysSortServiceImpl implements SysSortService {

	@Autowired
	private SysSortRepository sysSort;

	public List<SysSortInfo> findRootSortByType(String type) {
		List<SysSortInfo> list = this.sysSort.findRootSortList(type);
		return list;
	}

	public SysSortInfo findSortById(Long sortId) {
		return this.sysSort.find(sortId);
	}

	@Override
	public boolean createSysSort(Long parentId, String sortType, String sortName) {
		SysSortInfo sort=new SysSortInfo();
		sort.setSortType(SORT_TYPE.valueOf(sortType));
		sort.setSortName(sortName);
		sort.setParentId(parentId);
		return this.sysSort.save(sort);
	}

	@Override
	public boolean modifySysSort(Long id, SysSortInfo sort) {
		SysSortInfo pojo=this.findSortById(id);
		pojo.setSortName(sort.getSortName());
		return this.sysSort.save(pojo);
	}

	@Override
	public boolean removeSysSort(Long id) {
		SysSortInfo sort=this.findSortById(id);
		switch (sort.getSortType()) {
		case template_index:
			if(!sort.getIndexs().isEmpty())
				throw new BusinessException(String.format("当前分类：%s 正在被首页模版使用中，不能直接删除。", sort.getSortName()));
			break;
		case template_list:
			if(!sort.getLists().isEmpty())
				throw new BusinessException(String.format("当前分类：%s 正在被列表模版使用中，不能直接删除。", sort.getSortName()));
			break;
		case template_content:
			if(!sort.getContents().isEmpty())
				throw new BusinessException(String.format("当前分类：%s 正在被内容模版使用中，不能直接删除。", sort.getSortName()));
			break;
		case template_custom:
			if(!sort.getCustoms().isEmpty())
				throw new BusinessException(String.format("当前分类：%s 正在被自定义模版使用中，不能直接删除。", sort.getSortName()));
			break;
		case template_snippet:
			if(!sort.getSnippets().isEmpty())
				throw new BusinessException(String.format("当前分类：%s 正在被模版片段使用中，不能直接删除。", sort.getSortName()));
			break;
		default:
			break;
		}
		if(!sort.getChildren().isEmpty())
			throw new BusinessException(String.format("当前分类：%s 存在子级分类，不能直接删除。", sort.getSortName()));
		return this.sysSort.remove(sort);
	}

	@Override
	public boolean findSortNameIsExists(String sortName, String sortType) {
		Search search=new Search();
		search.addFilterEqual("sortType", SORT_TYPE.valueOf(sortType));
		search.addFilterEqual("sortName", sortName);
		return this.sysSort.count(search)>0?true:false;
	}

	@Override
	public List<SysSortInfo> findSortsByType(SORT_TYPE type) {
		return this.sysSort.findSorts(type);
	}
	
	@Override
	public SearchResult<SysSortInfo> findSortList(SORT_TYPE sortType,int startIndex,int maxResults){
		Search search=new Search(SysSortInfo.class);
		if(sortType!=null){
			search.addFilterEqual("sortType", sortType);
		}
		search.addFilterNull("parentId");//只查询顶级的分配，然后在递归出其下的分类
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("createTime", true)));
		SearchResult<SysSortInfo> srs=this.sysSort.searchAndCount(search);
		List<SysSortInfo> newList=new ArrayList<>();
		for(SysSortInfo sort:srs.getResult()){
			newList.add(sort);
			recursionChildrenSort(newList, sort.getId());
		}
		srs.setResult(newList);
		return srs;
	}
	
	/**
	 * 根据上级分类ID递归其所有的子级分类
	 * @param parentId
	 * */
	private void recursionChildrenSort(List<SysSortInfo> list,Long parentId){
		List<SysSortInfo> childrenList=this.sysSort.search(new Search().addFilterEqual("parentId", parentId));
		for(SysSortInfo childrenSort:childrenList){
			list.add(childrenSort);
			if(!this.sysSort.search(new Search().addFilterEqual("parentId", childrenSort.getId())).isEmpty()){
				recursionChildrenSort(list,childrenSort.getId());
			}
		}
	}

}
