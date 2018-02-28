package com.dw.suppercms.application.plugin.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.data.DataService;
import com.dw.suppercms.application.modules.ColumnService;
import com.dw.suppercms.application.plugin.HitsService;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.plugin.HitsHistory;
import com.dw.suppercms.domain.plugin.HitsHistoryRepository;
import com.dw.suppercms.domain.plugin.HitsInfo;
import com.dw.suppercms.domain.plugin.HitsInfoRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.utils.TopData;
import com.googlecode.genericdao.search.Search;

@ApplicationService
public class HitsServiceImpl implements HitsService {
	
	@Resource
	private HitsInfoRepository hitsInfoRepository;
	@Resource
	private HitsHistoryRepository hitsHistoryRepository;
	@Resource
	private ManuscriptMemberRepository manuscriptMemberRepository;
	@Resource
	private ColumnService columnService;
	@Resource
	private DataService dataService;

	@Override
	public Integer saveHits(Long siteId, Long columnId, String link, String ip,
			Long recordId) {
		Column column=columnService.retrieve(columnId);
		Map<String,Object> dataMap=this.dataService.retrieve(column.getDatasourceId(), recordId);
		String title=dataMap.isEmpty()?"":dataMap.get("title").toString();
		Integer count=1;
		HitsInfo hits=this.findHitsByColumnIdAndRecordId(columnId, recordId);
		if(hits==null){
			Object manuId=dataMap.isEmpty()?null:dataMap.get("manu_id");
			Object userId=dataMap.isEmpty()?null:dataMap.get("create_user_id");
			hits = new HitsInfo();
			hits.setSiteId(siteId);
			hits.setColumnId(columnId);
			hits.setTitle(title);
			hits.setRecordId(recordId);
			hits.setLink(link);
			//TODO:设置单位名称,以及稿件ID(如果是投稿的文章)
			if(userId!=null){
				ManuscriptMember member=manuscriptMemberRepository.find(Long.parseLong(userId.toString()));
				if(member!=null){
					hits.setOrgan(member.getOrgan());
				}
			}
			if(manuId!=null){
				hits.setManuId(Long.parseLong(manuId.toString()));
			}
			hits.setCount(count);
			hitsInfoRepository.save(hits);
		}else{
			Integer num=hits.getCount();
			count=num==null?0:num.intValue()+1;
			if(StringUtils.isNotEmpty(link)){
				hits.setLink(link);
			}
			hits.setTitle(title);
			hits.setCount(count);
			hitsInfoRepository.save(hits);
		}
		
		HitsHistory history=new HitsHistory();
		history.setIp(ip);
		history.setCreateTime(new Date());
		history.setHid(hits.getHid());
		this.hitsHistoryRepository.save(history);

		return count;
	}

	@Override
	public Integer findHitsCountByColumnIdAndRecordId(Long columnId,
			Long recordId) {
		HitsInfo obj= this.findHitsByColumnIdAndRecordId(columnId, recordId);
		if(obj==null){
			return 0;
		}
		return obj.getCount();
	}

	@Override
	public HitsInfo findHitsByColumnIdAndRecordId(Long columnId, Long recordId) {
		Search search=new Search().addFilterEqual("recordId", recordId).addFilterEqual("columnId", columnId);
		List<HitsInfo> list=this.hitsInfoRepository.search(search);
		if(!org.springframework.util.CollectionUtils.isEmpty(list)){
			return list.get(0);
		}
		return null;
	}

	@Override
	public void removeHitsByRecordId(Long recordId) {
		List<HitsInfo> hitsList=this.hitsInfoRepository.search(new Search().addFilterEqual("recordId", recordId));
		if(!org.springframework.util.CollectionUtils.isEmpty(hitsList)){
			for(HitsInfo hits:hitsList){
				List<HitsHistory> list=hitsHistoryRepository.search(new Search().addFilterEqual("hid", hits.getHid()));
				if(!CollectionUtils.isEmpty(list)){
					for(HitsHistory hitsHistory:list){
						this.hitsHistoryRepository.remove(hitsHistory);
					}
				}
			}
			this.hitsInfoRepository.remove((HitsInfo[]) hitsList.toArray());
		}
	}
	
	@Override
	public List<TopData> findHitsByTop(String siteId,String columnId,boolean isOrgan,Integer topNum,Pager pager){
		List<TopData> topDatas = new ArrayList<TopData>();
		String format="yyyy-MM-dd";
		String startTime=getApartDay((topNum+1), "sub", format);//开始时间为topNum再往前推一天,计算是大于
		String endTime=getApartDay((1), "add", format); //结束时间为明天，计算到今天当前时间为止
		List<HitsInfo> list=this.hitsInfoRepository.findHitsTopList(siteId,columnId,isOrgan,startTime, endTime, pager);
		Long rowCount=this.hitsInfoRepository.findHitsTopListCount(siteId, columnId,isOrgan,startTime, endTime);
		pager.calcPageCount(rowCount);
		TopData topData =null;
		for (HitsInfo praise : list) {
			topData = new TopData();
			if(praise.getOrgan()!=null){
				topData.setOrganName(praise.getOrgan().getOrganName());
			}
			topData.setLink(praise.getLink());
			topData.setTitle(praise.getTitle());
			topData.setNum(praise.getPs());
			topDatas.add(topData);
		}
		return topDatas;
	}


	@Override
	public Integer findHitsCountByOrgan(Long userId) {
		try {
			return this.hitsInfoRepository.findHitsCountByOrgan(userId).intValue();
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public Integer findHitsCountByRecordId(Long recordId) {
		try {
			return this.hitsInfoRepository.findHitsByRecordId(recordId).intValue();
		} catch (Exception e) {
		}
		return 0;
	}
	
	private static String getApartDay(Integer num,String expression,String partten){
		Calendar calendar = Calendar.getInstance();
		if("sub".equals(expression)){
			calendar.add(Calendar.DAY_OF_MONTH, -num);
		}else{
			calendar.add(Calendar.DAY_OF_MONTH, num);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(partten);
		return dateFormat.format(calendar.getTime());
		
	}

}
