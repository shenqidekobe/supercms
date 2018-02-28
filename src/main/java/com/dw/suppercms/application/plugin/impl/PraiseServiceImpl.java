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
import com.dw.suppercms.application.plugin.PraiseService;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptMemberRepository;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.plugin.PraiseHistory;
import com.dw.suppercms.domain.plugin.PraiseHistoryRepository;
import com.dw.suppercms.domain.plugin.PraiseInfo;
import com.dw.suppercms.domain.plugin.PraiseRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.utils.TopData;
import com.googlecode.genericdao.search.Search;

@ApplicationService
public class PraiseServiceImpl implements PraiseService {

	@Resource
	private PraiseRepository praiseRepository;
	@Resource
	private PraiseHistoryRepository praiseHistoryRepository;
	@Resource
	private ManuscriptMemberRepository manuscriptMemberRepository;
	@Resource
	private ColumnService columnService;
	@Resource
	private DataService dataService;

	@Override
	public Integer savePraise(Long siteId,Long columnId, String link,String ip, Long recordId) {
		Column column=columnService.retrieve(columnId);
		Map<String,Object> dataMap=this.dataService.retrieve(column.getDatasourceId(), recordId);
		String title=dataMap.isEmpty()?"":dataMap.get("title").toString();
		Integer count=1;
		PraiseInfo praise=findPraiseByColumnIdAndRecordId(columnId, recordId);
		if(praise==null){
			Object manuId=dataMap.isEmpty()?null:dataMap.get("manu_id");
			Object userId=dataMap.isEmpty()?null:dataMap.get("create_user_id");
			praise = new PraiseInfo();
			praise.setSiteId(siteId);
			praise.setColumnId(columnId);
			praise.setTitle(title);
			praise.setRecordId(recordId);
			praise.setLink(link);
			praise.setCount(count);
			//TODO:设置单位名称,以及稿件ID(如果是投稿的文章)
			if(userId!=null){
				ManuscriptMember member=manuscriptMemberRepository.find(Long.parseLong(userId.toString()));
				if(member!=null){
					praise.setOrgan(member.getOrgan());
				}
			}
			if(manuId!=null){
				praise.setManuId(Long.parseLong(manuId.toString()));
			}
			praiseRepository.save(praise);
		}else{
			Integer num=praise.getCount();
			count=num==null?0:num.intValue()+1;
			if(StringUtils.isNotEmpty(link)){
				praise.setLink(link);
			}
			praise.setCount(count);
			praiseRepository.save(praise);
		}
		
		PraiseHistory history=new PraiseHistory();
		history.setIp(ip);
		history.setCreateTime(new Date());
		history.setPid(praise.getPid());
		this.praiseHistoryRepository.save(history);

		return count;
	}
	
	@Override
	public PraiseInfo findPraiseByColumnIdAndRecordId(Long columnId,Long recordId){
		Search search=new Search().addFilterEqual("recordId", recordId).addFilterEqual("columnId", columnId);
		List<PraiseInfo> list=this.praiseRepository.search(search);
		if(!org.springframework.util.CollectionUtils.isEmpty(list)){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public Integer findPraiseCountByColumnIdAndRecordId(Long columnId,Long recordId){
		PraiseInfo obj= this.findPraiseByColumnIdAndRecordId(columnId, recordId);
		if(obj==null){
			return 0;
		}
		return obj.getCount();
	}

	@Override
	public List<TopData> findPraisesByTop(String siteId,String columnId,boolean isOrgan,Integer topNum,Pager pager){
		List<TopData> topDatas = new ArrayList<TopData>();
		String format="yyyy-MM-dd";
		String startTime=getApartDay((topNum+1), "sub", format);//开始时间为topNum再往前推一天,计算是大于
		String endTime=getApartDay((1), "add", format); //结束时间为明天，计算到今天当前时间为止
		List<PraiseInfo> list=this.praiseRepository.findPraiseTopList(siteId,columnId,isOrgan,startTime, endTime, pager);
		Long rowCount=this.praiseRepository.findPraiseTopListCount(siteId, columnId,isOrgan,startTime, endTime);
		pager.calcPageCount(rowCount);
		TopData topData =null;
		for (PraiseInfo praise : list) {
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
	public void removePraiseByRecordId(Long recordId) {
		List<PraiseInfo> praiseList=this.praiseRepository.search(new Search().addFilterEqual("recordId", recordId));
		if(!org.springframework.util.CollectionUtils.isEmpty(praiseList)){
			for(PraiseInfo prise:praiseList){
				List<PraiseHistory> list=this.praiseHistoryRepository.search(new Search().addFilterEqual("pid", prise.getPid()));
				if(!CollectionUtils.isEmpty(list)){
					for(PraiseHistory praiseHistory:list){
						this.praiseHistoryRepository.remove(praiseHistory);
					}
				}
			}
			this.praiseRepository.remove((PraiseInfo[]) praiseList.toArray());
		}
	}

	@Override
	public Integer findPraiseCountByOrgan(Long userId) {
		try {
			return this.praiseRepository.findPraiseCountByOrgan(userId).intValue();
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public Integer findPraiseCountByRecordId(Long recordId) {
		try {
			return this.praiseRepository.findPraiseByRecordId(recordId).intValue();
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
