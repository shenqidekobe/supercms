package com.dw.suppercms.application.plugin;

import java.util.List;

import com.dw.suppercms.domain.plugin.HitsInfo;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.utils.TopData;

/**
 * 文章点击量服务接口
 * */
public interface HitsService {
	
	/**
	 * 保存点击量
	 * */
	public Integer saveHits(Long siteId,Long columnId,String link,String ip, Long recordId);
	
	/**
	 * 按栏目获取文章点击量
	 * */
	public Integer findHitsCountByColumnIdAndRecordId(Long columnId,Long recordId);
	
	/**
	 * 获取点击量内容
	 * */
	public HitsInfo findHitsByColumnIdAndRecordId(Long columnId,Long recordId);
	
	/**
	 * 按文章ID删除浏览记录
	 * */
	public void removeHitsByRecordId(Long recordId);
	
	/**
	 * 检索点赞排行记录
	 * @params 站点ID，栏目ID，是否单位排行，多少天内的排行，分页信息
	 * */
	public List<TopData> findHitsByTop(String siteId,String columnId,boolean isOrgan,Integer topNum,Pager pager);
	
	/**
	 * 获取单位发的稿件的点赞总数
	 * */
	public Integer findHitsCountByOrgan(Long userId);
	
	/**
	 * 获取文章的总点赞数
	 * */
	public Integer findHitsCountByRecordId(Long recordId);

}
