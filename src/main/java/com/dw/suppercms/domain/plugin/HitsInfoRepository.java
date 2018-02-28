package com.dw.suppercms.domain.plugin;

import java.util.List;

import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface HitsInfoRepository extends GenericDAO<HitsInfo,Long>{
	
	/**
	 * 检索时间段的排行榜
	 * 1：可检索某站点的文章排行榜
	 * 2：可检索某个栏目的文章排行榜
	 * 3：可检索单位点赞排行榜
	 * */
	public List<HitsInfo> findHitsTopList(String siteId,String columnId,boolean isOrgan,String startTime,String endTime,Pager pager);
	
	/**
	 * 检索时间段的排行榜总数
	 * */
	public Long findHitsTopListCount(String siteId,String columnId,boolean isOrgan,String startTime,String endTime);
	
	/**
	 * 检索时间段点赞的数量(按点赞的文章)
	 * */
	public Long findHitsTopCountByPid(Long pid,String startTime,String endTime);
	
	/**
	 * 检索单位的点赞数量
	 * */
	public Long findHitsCountByOrgan(Long userId);
	
	/**
	 * 获取文章的点赞总数
	 * */
	public Long findHitsByRecordId(Long recordId);
	

}
