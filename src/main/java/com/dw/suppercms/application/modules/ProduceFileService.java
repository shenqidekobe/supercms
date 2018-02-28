package com.dw.suppercms.application.modules;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.data.DataRecord;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 生成文件调用的接口
 * */
public interface ProduceFileService {
	
	/**
	 * 根据ID获取站点信息
	 * */
	Site findSiteById(Long siteId);
	
	/**
	 * 根据一组站点ID获取站点列表
	 * @param siteIds
	 * @return List<Site>
	 * */
	List<Site> findSiteByIds(String siteIds);
	
	/**
	 * 获取所有的站点信息
	 * */
	List<Site> findAllSites();
	
	/**
	 * 获取模版信息
	 * @param templateId
	 * @return TemplateInfo
	 * */
	TemplateInfo findTemplateInfoById(Long templateId);
	
	/**
	 * 根据模版片段tag获取模版片段信息
	 * @param snippetTag
	 * @return TemplateSnippet
	 * */
	TemplateSnippet findTemplateSnippetByTag(String snippetTag);
	
	/**
	 * 获取自定义页信息
	 * @param customId
	 * @return Custom
	 * */
	Custom findCustomById(Long customId);
	
	/**
	 * 根据一组自定义页ID获取列表
	 * @param customIds
	 * @return List<Custom>
	 * */
	List<Custom> findCustomByIds(String customIds);
	
	/**
	 * 获取所有的自定义页
	 * */
	List<Custom> findAllCustoms();
	
	/**
	 * 获取栏目信息
	 * @param columnId
	 * @return Column
	 * */
	Column findColumnById(Long columnId);
	
	/**
	 * 更新栏目生成列表状态
	 * @param columnId
	 * @param makeListState
	 * @return Column
	 * */
	Column modifyColumnListState(Long columnId,boolean makeListState);
	
	/**
	 * 获取栏目的生成列表状态
	 * @param columnId
	 * @return boolean
	 * */
	boolean findColumnListState(Long columnId);
	
	/**
	 * 根据一组栏目ID获取栏目列表
	 * @param columnIds
	 * @return List<Column>
	 * */
	List<Column> findColumnByIds(String columnIds);
	
	/**
	 * 根据用户所能管理的数据源查询栏目列表
	 * @param userId
	 * @return List<Column>
	 * */
	List<Column> findColumnByUser(Long userId);
	
	/**
	 * 根据站点ID得到站点下的栏目列表
	 * */
	List<Column> findColumnBySiteId(Long siteId);
	
	/**
	 * 查询所有的栏目
	 * */
	List<Column> findAllColumns();
	
	/**
	 * 根据数据源ID得到栏目列表
	 * */
	List<Column> findColumnByDatasourceId(Long datasourceId);
	
	/**
	 * 查询数据源
	 * */
	Datasource findDatasourceById(Long id);
	
	/**
	 * 获取当前栏目列表ID的关联列表(父级)
	 * @param parentIds
	 * @return List<Long>
	 * */
	List<Long> queryColumnTreeByParentIds(List<Long> parentIds);
	
	/**
	 * 更新栏目数据的生成状态
	 * @param columnId
	 * @param dataId
	 * @param dataSourceId
	 * @param produceState
	 * */
	void modifyColumnDataProduceState(Long columnId,Long dataId,Long dataSourceId,boolean produceState);
	
	/**
	 * 查询数据
	 * */
	DataRecord findDataRecord(Long datasourceId,Long id);
	
	/**
	 * 检索数据列表，数据指令使用
	 * params作为请求查询参数集合,columnIds为栏目ID集合，startTime和endTime为数据的添加开始时间和结束时间
	 * @param pager
	 * @param params
	 * 
	 * @return SearchResult<DataRecord>
	 * */
	SearchResult<DataRecord> findDataRecordListUseDirective(Map<String,Object> params,List<Long> columnIds,Date startTime,Date endTime,Pager pager);

}
