package com.dw.suppercms.application.modules.impl;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.CustomRepository;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.modules.SiteRepository;
import com.dw.suppercms.domain.security.UserDataSource;
import com.dw.suppercms.domain.security.UserDataSourceRepository;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.domain.templates.TemplateRepository;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.domain.templates.TemplateSnippetRepository;
import com.dw.suppercms.infrastructure.common.ConstantConfig;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.data.DataRecordTransformConvetor;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 生成文件所需的服务接口
 * @author kobe
 * @version 1.0
 * */
@ApplicationService
public class ProduceFileServiceImpl implements ProduceFileService{
	
	@Resource
	private SiteRepository siteRepository;
	
	@Resource
	private TemplateRepository templateRepository;
	
	@Resource
	private CustomRepository customRepository;
	
	@Resource
	private TemplateSnippetRepository templateSnippetRepository;
	
	@Resource
	private ColumnRepository columnRepository;
	
	@Resource
	private ColumnDataRepostitory columnDataRepostitory;
	
	@Resource
	private DataRepository dataRepository;
	
	@Resource
	private DatasourceRepository datasourceRepository;
	
	@Resource
	private UserDataSourceRepository userDataSourceRepository;
	
	@Resource
	private ConstantConfig constantConfig;
	
	@Override
	public Site findSiteById(Long siteId){
		return this.siteRepository.find(siteId);
	}
	
	@Override
	public List<Site> findSiteByIds(String siteIds){
		assertNotNull(siteIds);
		Search search=new Search();
		search.addFilterIn("id", Arrays.asList(StringUtils.split(siteIds,",")));
		return this.siteRepository.search(search);
	}
	

	@Override
	public TemplateInfo findTemplateInfoById(Long templateId) {
		return templateRepository.find(templateId);
	}

	@Override
	public Custom findCustomById(Long customId) {
		return customRepository.find(customId);
	}
	
	@Override
	public List<Custom> findCustomByIds(String customIds){
		Search search=new Search();
		search.addFilterIn("id",Arrays.asList(StringUtils.split(customIds,",")));
		return this.customRepository.search(search);
	}

	@Override
	public TemplateSnippet findTemplateSnippetByTag(String snippetTag) {
		Search search=new Search();
		search.addFilterEqual("snippetTag", snippetTag);
		return this.templateSnippetRepository.searchUnique(search);
	}

	@Override
	public Column findColumnById(Long columnId) {
		return columnRepository.find(columnId);
	}
	
	@Override
	public Column modifyColumnListState(Long columnId,boolean makeListState){
		assertNotNull(columnId);

		Column dbColumn = columnRepository.find(columnId);
		dbColumn.setMakeListState(makeListState);
		columnRepository.save(dbColumn);
		return dbColumn;
	}
	
	@Override
	public boolean findColumnListState(Long columnId){
		assertNotNull(columnId);

		Column dbColumn = columnRepository.find(columnId);
		
		return dbColumn.getMakeListState();
	}
	
	@Override
	public List<Column> findColumnByIds(String columnIds){
		Search search=new Search();
		search.addFilterIn("id",Arrays.asList(StringUtils.split(columnIds,",")));
		return this.columnRepository.search(search);
	}
	
	@Override
	public List<Site> findAllSites() {
		return this.siteRepository.findAll();
	}

	@Override
	public List<Custom> findAllCustoms() {
		return this.customRepository.findAll();
	}
	
	@Override
    public List<Column> findColumnByUser(Long userId){
		//get user-->get user dataSource-->in dataSourceId
		List<UserDataSource> list=this.userDataSourceRepository.search(new Search().addFilterEqual("userId", userId));
		if(list.isEmpty()){
			return new ArrayList<>();
		}
		List<Long> datasourceIds=new ArrayList<>();
		for(UserDataSource uds:list){
			datasourceIds.add(uds.getDatasourceId());
		}
		return this.columnRepository.search(new Search().addFilterIn("datasourceId", datasourceIds));
	}
	
	@Override
	public List<Column> findColumnBySiteId(Long siteId){
		return this.columnRepository.search(new Search().addFilterEqual("siteId", siteId));
	}
	
	@Override
	public List<Column> findAllColumns(){
		return this.columnRepository.findAll();
	}
	
	@Override
	public List<Column> findColumnByDatasourceId(Long datasourceId){
		return this.columnRepository.search(new Search().addFilterEqual("datasourceId", datasourceId));
	}
	
	@Override
	public Datasource findDatasourceById(Long id){
		return this.datasourceRepository.find(id);
	}
	
	@Override
	public List<Long> queryColumnTreeByParentIds(List<Long> parentIds) {
		Set<Long> allColumnIds = new HashSet<Long>();
		columnRepository.queryColumnTreeByParentIds(parentIds, allColumnIds); 
		return new ArrayList<Long>(allColumnIds);
	}
	
	@Override
	public void modifyColumnDataProduceState(Long columnId,Long dataId,Long dataSourceId,boolean produceState){
		Search search=new Search();
		search.addFilterEqual("columnId", columnId);
		search.addFilterEqual("datasourceId", dataSourceId);
		search.addFilterEqual("dataId", dataId);
		ColumnData columnData=this.columnDataRepostitory.searchUnique(search);
		if(columnData==null){
			columnData=new ColumnData();
			columnData.setColumnId(columnId);
			columnData.setDataId(dataId);
			columnData.setDatasourceId(dataSourceId);
		}
		columnData.setProduceState(produceState);
		columnData.setProduceDate(new Date());
		this.columnDataRepostitory.save(columnData);
	}
	
	@Override
	public DataRecord findDataRecord(Long datasourceId,Long id){
		Model model=this.findDatasourceById(datasourceId).getModel();
		String tableCode=model.getTableCode();
		Map<String,Object> dataMap=this.dataRepository.findDynamicData(tableCode, id);
		DataRecord dataRecord=DataRecordTransformConvetor.dataMapToDataRecord(dataMap,constantConfig.getWebsitePublishImgServer());
		return dataRecord;
	}

	@Override
	public SearchResult<DataRecord> findDataRecordListUseDirective(
			Map<String, Object> params, List<Long> columnIds, Date startTime,
			Date endTime, Pager pager) {
		//解析params查询参数map
		SearchResult<DataRecord> searchResult=new SearchResult<>();
		if(columnIds.isEmpty())return searchResult;
		//取出columnIds的第一个columnId作为tableCode的源
		Model model=this.columnRepository.find(columnIds.get(0)).getDatasource().getModel();
		String tableCode=model.getTableCode();
		List<Map<String,Object>> dataMapList=this.dataRepository.getListDataToRecordDirective(model, params, columnIds, startTime, endTime, pager);
		List<DataRecord> results=new ArrayList<>();
		//组装数据转换为DataRecord
		for(Map<String,Object> dataMap:dataMapList){
			results.add(DataRecordTransformConvetor.dataMapToDataRecord(dataMap,constantConfig.getWebsitePublishImgServer()));
		}
		int totalCount=this.dataRepository.getListDataCountToRecordDirective(tableCode, params, columnIds, startTime, endTime);
		searchResult.setResult(results);
		searchResult.setTotalCount(totalCount);
		return searchResult;
	}

}
