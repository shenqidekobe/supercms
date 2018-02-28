package com.dw.suppercms.application.index.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dw.party.search.support.dto.ArticleSupport;
import com.dw.party.search.support.webservice.IIndexRemoteService;
import com.dw.suppercms.application.index.IndexSearchService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.infrastructure.common.ConstantConfig;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;

@Service
public class IndexSearchServiceImpl implements IndexSearchService{
	
	private Logger log=LoggerFactory.getLogger(getClass());

	@Autowired
	private IIndexRemoteService indexRemoteService;
	
	@Autowired
	private DataRepository dataRepository;
	
	@Autowired
	private DatasourceRepository datasourceRepository;
	
	@Autowired
	private ConstantConfig constantConfig;
	
	@Autowired
	private ProduceFileService produceFileService;
	
	private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public void pushAll(){
		String siteIds=constantConfig.getDataIndexSiteIds();
		List<Site> sites=produceFileService.findSiteByIds(siteIds);
		for(Site site:sites){
			List<Column> columns=produceFileService.findColumnBySiteId(site.getId());
			for(Column column:columns){
				Datasource datasource=column.getDatasource();
				String tableCode=datasource.getModel().getTableCode();
				List<Map<String, Object>> datas=this.dataRepository.getListData(tableCode, null);
				for(Map<String, Object> dataMap:datas){
					pushIndex(datasource.getColumns(), dataMap, Long.parseLong(dataMap.get("id").toString()));
				}
			}
		}
	}
	
	@Override
	public void pushData(Long datasourceId,Long id) {
		log.debug("开始调用索引服务器的推送数据接口。");
		try {
			Datasource datasource=this.datasourceRepository.find(datasourceId);
			String tableCode=datasource.getModel().getTableCode();
			Map<String, Object> dataMap=this.dataRepository.findDynamicData(tableCode, id);
			Set<Column> columns=datasource.getColumns();
			pushIndex(columns, dataMap, id);
		} catch (Exception e) {
			log.error("推送数据到索引服务器出现异常："+e.getMessage());
		}
	}

	@Override
	public void popData(List<String> popIds) {
		log.debug("开始调用索引服务的删除数据接口."+popIds.size());
		indexRemoteService.removeArticleIndex(popIds);
	}
	
	private void pushIndex(Set<Column> columns,Map<String, Object> dataMap,Long id){
		List<ArticleSupport> toPush = new ArrayList<ArticleSupport>();
		try {
			ArticleSupport article = null;
			for (Column column : columns) {
				article = new ArticleSupport();
				article = dataMapToArticleSupport(dataMap, article, column, id);
				toPush.add(article);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		indexRemoteService.addArticleIndex(toPush);
	}

	
	/**
	 * 数据Map转换为ArticleSupport
	 * */
	private ArticleSupport dataMapToArticleSupport(Map<String,Object> dataMap,ArticleSupport article,Column column,Long dataId)throws Exception{
		article.setId(dataId);
		article.setSiteId(column.getSiteId());
		article.setDataType(ArticleSupport.DATA_TYPE.ARTICLE.name());
		article.setSource("");
		Date publishTime = null;
		String urlRef = "",content="",intro="";
		for (String key : dataMap.keySet()) {
			Object value=dataMap.get(key);
			String obj=value==null?"":value.toString();
			switch (key) {
			case "content":
				content = CommonsUtil.delHTMLTag(obj);
				article.setContent(content);
				break;
			case "title":
				article.setTitle(obj);
				break;
			case "editor":
				article.setEditor(obj);
				break;
			case "intro":
				intro=obj;
				article.setIntro(obj);
				break;
			case "urlRef":
				urlRef=obj;
				break;
			case "create_time":
				publishTime=value==null?null:format.parse(obj);
				article.setPublishTime(publishTime);
				break;
			case "writer":
				article.setWriter(obj);
				break;
			}
		}
		if(StringUtils.length(content) < 2){
			article.setContent(intro);
		}
		article.setColumnId(column.getId());
		article.setColumnName(column.getTitle());
		String url = StringUtils.isNotEmpty(urlRef) ? urlRef : column.getContentWebPath(dataId.toString(), new Timestamp(publishTime.getTime()));
		if(!StringUtils.startsWith(url, "http")){
			url = column.getSite().getProductDomain() + url;
		}
		article.setUrl(url);
		return article;
	}
}
