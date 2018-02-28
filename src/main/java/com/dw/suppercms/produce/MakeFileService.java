package com.dw.suppercms.produce;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.data.DataRecord;
import com.dw.suppercms.produce.execption.MakeContentExecption;
import com.dw.suppercms.produce.execption.MakeCustomExecption;
import com.dw.suppercms.produce.execption.MakeIndexExecption;
import com.dw.suppercms.produce.execption.MakeListExecption;

/**
 * 生成文件的外部接口
 * */
public abstract interface MakeFileService {
	
	/**
	 * 单个生成首页任务
	 * @param site
	 * @return makeFileResult
	 * */
	MakeFileResult makeIndexFile(Site site)throws MakeIndexExecption;
	
	/**
	 * 批量生成首页，返回一个携带结果的CompletionService
	 * @param sites
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeIndexFile(List<Site> sites)throws MakeIndexExecption;
	
	/**
	 * 批量生成首页任务
	 * @params sites
	 * */
	MakeFileResult makeIndexFileTask(List<Site> sites)throws MakeIndexExecption;
	
	/**
	 * 生成列表页
	 * @param column
	 * @return makeFileResult
	 * */
	MakeFileResult makeListFile(Column column)throws MakeListExecption;
	
	/**
	 * 批量生成栏目列表页
	 * @param columns
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeListFile(List<Column> columns,Map<String,Object> paramMap)throws MakeListExecption;
	
	/**
	 * 生成栏目历史列表页
	 * @param column
	 * @param preCount
	 * 
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeHistoryListFile(Column column,Integer preCount)throws MakeListExecption;
	
	/**
	 * 生成栏目历史列表任务
	 * @param column
	 * @param preCount
	 * 
	 * @return MakeFileResult
	 * */
	MakeFileResult makeHistoryListFileTask(Column column,Integer preCount)throws MakeListExecption;
	

	/**
	 * 批量生成栏目历史列表任务
	 * @param columns
	 * @param historyListMethod
	 * @param preCount
	 * 
	 * @return MakeFileResult
	 * */
	MakeFileResult makeHistoryListFileTask(List<Column> column,Integer preCount)throws MakeListExecption;
	
	
	/**
	 * 批量生成栏目列表任务
	 * @param columns
	 * @param paramMap
	 * */
	MakeFileResult makeListFileTask(List<Column> columns,Map<String,Object> paramMap)throws MakeListExecption;
	
	/**
	 * 生成某条数据的所有的内容页
	 * @param dataRecord
	 * @param columns
	 * @return makeFileResult
	 * */
	MakeFileResult makeContentFileTask(DataRecord dataRecord,List<Column> column)throws MakeContentExecption;
	
	/**
	 * 生成某条数据的所有的内容页
	 * @param dataRecord
	 * @param columns
	 * @return CompletionService
	 * */
	CompletionService<MakeFileResult> makeContentFile(DataRecord dataRecord,List<Column> column)throws MakeContentExecption;
	
	/**
	 * 生成某条数据所在栏目的内容页
	 * @param column
	 * @param dataRecord
	 * @return makeFileResult
	 * */
	MakeFileResult makeContentFile(Column column,DataRecord dataRecord)throws MakeContentExecption;
	
	/**
	 * 生成栏目的所有内容页
	 * @param column
	 * @param dataRecords
	 * @return makeFileResult
	 * */
	MakeFileResult makeContentFile(Column column,List<DataRecord> dataRecords)throws MakeContentExecption;
	
	/**
	 * 生成栏目的所有内容页返回函数
	 * @param column
	 * @param dataRecords
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeContentFileGoCompletion(Column column,List<DataRecord> dataRecords)throws MakeContentExecption;
	
	/**
	 * 批量生成栏目的内容页
	 * @param columns
	 * @param dataMap{Column-dataRecords}
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeContentFile(List<Column> columns,Map<Column,List<DataRecord>> dataMap)throws MakeContentExecption;
	
	/**
	 * 批量生成栏目内容任务
	 * @param columns
	 * @param dataMap
	 * */
	MakeFileResult makeContentFileTask(List<Column> columns,Map<Column,List<DataRecord>> dataMap)throws MakeContentExecption;
	
	/**
	 * 批量生成栏目内容以及列表任务
	 * @param columns
	 * @param dataMap
	 * */
	MakeFileResult makeContentAndListFileTask(List<Column> columns,Map<Column,List<DataRecord>> dataMap)throws MakeContentExecption;
	
	/**
	 * 生成自定义页
	 * @param custom
	 * @return makeFileResult
	 * */
	MakeFileResult makeCustomFile(Custom custom)throws MakeCustomExecption;
	
	/**
	 * 批量生成自定义页
	 * @param customs
	 * @return CompletionService<makeFileResult>
	 * */
	CompletionService<MakeFileResult> makeCustomFile(List<Custom> customs)throws MakeCustomExecption;
	
	/**
	 * 批量生成自定义页任务
	 * @param customs
	 * */
	MakeFileResult makeCustomFileTask(List<Custom> customs)throws MakeCustomExecption;
	
	/**
	 * 生成所有
	 * @return makeFileResult
	 * */
	MakeFileResult makeAllFile()throws MakeIndexExecption,MakeListExecption,MakeContentExecption,MakeCustomExecption;
	
	/**
	 * 生成某个站点的所有页面
	 * @param site
	 * @return makeFileResult
	 * */
	MakeFileResult makeSiteTotalFile(Site site)throws MakeIndexExecption,MakeListExecption,MakeContentExecption,MakeCustomExecption;
	
	/**
	 * 生成某个栏目的所有列表页和内容页
	 * @param column
	 * @return makeFileResult
	 * */
	MakeFileResult makeColumnTotalFile(Column column)throws MakeListExecption,MakeContentExecption;
	
	/**
	 * 预览内容页
	 * @param dataRecord
	 * @param column
	 * @return inputStream
	 * */
	InputStream previewContentFile(DataRecord dataRecord,Column column)throws MakeContentExecption;
	
	

}
