package com.dw.suppercms.application.plugin;

import java.util.List;

import com.dw.suppercms.domain.plugin.PraiseInfo;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.utils.TopData;

public interface PraiseService {

	/**
	 * 保存点赞记录
	 * */
	public Integer savePraise(Long siteId,Long columnId,String link,String ip, Long recordId);
	
	/**
	 * 按栏目ID和数据ID检索该记录的点赞数量
	 * */
	public Integer findPraiseCountByColumnIdAndRecordId(Long columnId,Long recordId);
	
	/**
	 * 按栏目ID和数据ID检索点赞记录
	 * */
	public PraiseInfo findPraiseByColumnIdAndRecordId(Long columnId,Long recordId);

	/**
	 * 检索点赞排行记录
	 * @params 站点ID，栏目ID，是否单位排行，多少天内的排行，分页信息
	 * */
	public List<TopData> findPraisesByTop(String siteId,String columnId,boolean isOrgan,Integer topNum,Pager pager);
	
	/**
	 * 按文章ID删除点赞记录
	 * */
	public void removePraiseByRecordId(Long recordId);
	
	/**
	 * 获取单位发的稿件的点赞总数
	 * */
	public Integer findPraiseCountByOrgan(Long userId);
	
	/**
	 * 获取文章的总点赞数
	 * */
	public Integer findPraiseCountByRecordId(Long recordId);

}
