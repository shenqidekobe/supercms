package com.dw.suppercms.domain.plugin;

import java.util.List;

import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface PraiseRepository extends GenericDAO<PraiseInfo,Long> {

	/**
	 * 检索时间段的排行榜
	 * 1：可检索某站点的文章排行榜
	 * 2：可检索某个栏目的文章排行榜
	 * 3：可检索单位点赞排行榜
	 * */
	public List<PraiseInfo> findPraiseTopList(String siteId,String columnId,boolean isOrgan,String startTime,String endTime,Pager pager);
	
	/**
	 * 检索时间段的排行榜总数
	 * */
	public Long findPraiseTopListCount(String siteId,String columnId,boolean isOrgan,String startTime,String endTime);
	
	/**
	 * 检索时间段点赞的数量(按点赞的文章)
	 * */
	public Long findPraiseTopCountByPid(Long pid,String startTime,String endTime);
	
	/**
	 * 检索单位的点赞数量
	 * */
	public Long findPraiseCountByOrgan(Long userId);
	
	/**
	 * 获取文章的点赞总数
	 * */
	public Long findPraiseByRecordId(Long recordId);
	
}
