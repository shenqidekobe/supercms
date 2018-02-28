package com.dw.suppercms.application.data.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.SpringHelper;
import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.data.DataService;
import com.dw.suppercms.application.index.IndexSearchService;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;
import com.dw.suppercms.domain.data.Data.CheckStatus;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.Datasource.PassStatus;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelRepository;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.domain.security.User;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;

/**
 * 
 * DataServiceImpl
 *
 * @author osmos
 * @date 2015年8月12日
 */
@ApplicationService
public class DataServiceImpl
		implements DataService {

	@Autowired
	DataRepository dataRepository;
	@Autowired
	ModelRepository modelRepository;
	@Autowired
	ColumnRepository columnRepository;
	@Autowired
	ColumnDataRepostitory columnDataRepostitory;
	@Autowired
	DatasourceRepository datasourceRepository;
	@Autowired
	IndexSearchService indexSearchService;
	
	@Override
	public List<Map<String, Object>> paginateForManage(Long datasourceId, Map<String, Object> condition) {
		String tableCode = datasourceRepository.find(datasourceId).getModel().getTableCode();
		return dataRepository.paginateForManage(tableCode, datasourceId, condition);
	}

	@Override
	public int countForManage(Long datasourceId, Map<String, Object> condition) {

		String tableCode = datasourceRepository.find(datasourceId).getModel().getTableCode();
		return dataRepository.countForManage(tableCode, datasourceId, condition);
	}

	@Override
	public Map<String, Object> retrieve(Long datasourceId, Long id) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		return dataRepository.findDynamicData(model.getTableCode(), id);
	}

	@Override
	public void create(Map<String, Object> valuesMap) {

		Long datasourceId = Long.valueOf(valuesMap.get("datasource_id").toString());
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();

		// save dynamic data
		if(valuesMap.get("editor")==null){
			valuesMap.put("editor", ((User)SecurityUtils.getSubject().getPrincipal()).getName());
		}
		if(valuesMap.get("create_user_id")==null){
			valuesMap.put("create_user_id", ((User)SecurityUtils.getSubject().getPrincipal()).getId());
		}
		valuesMap.put("stick", false);
		valuesMap.put("datasource_id", datasourceId);
		valuesMap.put("model_id", model.getId());
		valuesMap.put("create_time", new Date());
		if(datasource.getPassStatus().equals(PassStatus.NO.name())){
			valuesMap.put("check_status", CheckStatus.PASS);
		}else{
			valuesMap.put("check_status", CheckStatus.ORIGIN);
		}
		dataRepository.saveDynamicData(model, valuesMap);

		// save ColumnData
		List<Column> columns = columnRepository.filterByDatasource(datasourceId);
		List<ColumnData> columnDatas = columns
				.stream()
				.map(c -> ColumnData.newOf(c.getId(), dataRepository.selectLastInsertId(), datasourceId, model.getId()))
				.collect(toList());
		columnDataRepostitory.save(columnDatas.toArray(new ColumnData[] {}));

	}

	@Override
	public void update(Map<String, Object> newValuesMap) {

		Long id = Long.valueOf(newValuesMap.get("id").toString());
		Long datasourceId = Long.valueOf(newValuesMap.get("datasource_id").toString());
		Datasource datasource = datasourceRepository.find(datasourceId);

		Model model = datasource.getModel();
		Map<String, Object> oldValuesMap = retrieve(datasourceId, id);

		dataRepository.updateDynamicData(model, oldValuesMap, newValuesMap);

		if (oldValuesMap.containsKey("headerPic") && oldValuesMap.get("headerPic") != null
				&& !Objects.equals(oldValuesMap.get("headerPic"), newValuesMap.get("headerPic"))) {
			deleteHeaderPic(oldValuesMap);
		}
	}

	@Override
	public void delete(Long id, Long datasourceId) {
		Datasource datasource = datasourceRepository.find(datasourceId);

		// delete headerPics of origin, big, small
		Map<String, Object> valuesMap = retrieve(datasourceId, id);
		if (valuesMap.containsKey("headerPic") && valuesMap.get("headerPic") != null) {
			deleteHeaderPic(valuesMap);
		}
		
		List<String> popIds = new ArrayList<String>();
		List<ColumnData> columnIds = columnDataRepostitory.search(new Search().addFilterEqual("dataId", id).addFilterEqual("datasourceId", datasourceId));
		for (ColumnData cd : columnIds) {
			String popId = cd.getColumnId() + "_" + id;
			popIds.add(popId);
		}

		// delete some data
		columnDataRepostitory.deleteByDataIdAndDatasourceId(id, datasourceId);
		dataRepository.deleteDynamicData(datasource.getModel().getTableCode(), id);

		// TODO delete index
		indexSearchService.popData(popIds);
	}

	@Override
	public void stick(Long id, Long datasourceId) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		dataRepository.stick(datasource.getModel().getTableCode(), id);
	}

	@Override
	public void unstick(Long id, Long datasourceId) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		dataRepository.unstick(datasource.getModel().getTableCode(), id);
	}

	@Override
	public void copy(Long id, Long datasourceId, List<Long> datasourceIds) {
		Datasource sourceDatasource = datasourceRepository.find(datasourceId);
		Model sourceModel = sourceDatasource.getModel();
		Map<String, Object> sourceData = dataRepository.findDynamicData(sourceModel.getTableCode(), id);
		List<Long> targetDatasouceIds = Lists.newArrayList(datasourceIds);
		targetDatasouceIds.stream().forEach(
				targetDatasourceId -> {
					Model targetModel = datasourceRepository.find(targetDatasourceId).getModel();
					Map<String, Object> targetData = targetModel
							.getFields()
							.stream()
							.collect(toMap(
									ModelField::getFieldCode,
									field -> {
										return sourceData.get(field.getFieldCode()) == null ? "null"
												: sourceData.get(field.getFieldCode());
									}));
					targetData.put("datasource_id", targetDatasourceId);
					this.create(targetData);

				});
	}

	@Override
	public void move(Long id, Long datasourceId, List<Long> datasourceIds) {
		Datasource sourceDatasource = datasourceRepository.find(datasourceId);
		Model sourceModel = sourceDatasource.getModel();
		Map<String, Object> sourceData = dataRepository.findDynamicData(sourceModel.getTableCode(), id);
		List<Long> targetDatasouceIds = Lists.newArrayList(datasourceIds);
		targetDatasouceIds.stream().forEach(
				targetDatasourceId -> {
					Model targetModel = datasourceRepository.find(targetDatasourceId).getModel();
					Map<String, Object> targetData = targetModel
							.getFields()
							.stream()
							.collect(toMap(
									ModelField::getFieldCode,
									field -> {
										return sourceData.get(field.getFieldCode()) == null ? "null"
												: sourceData.get(field.getFieldCode());
									}));
					targetData.put("editor", "todo");
					targetData.put("stick", false);
					targetData.put("datasource_id", targetDatasourceId);
					targetData.put("model_id", targetModel.getId());
					targetData.put("create_time", new Date());
					/** 1) save data */
					dataRepository.saveDynamicData(targetModel, targetData);
					/** 2) save column data */
					List<Column> columns = columnRepository.filterByDatasource(targetDatasourceId);
					List<ColumnData> columnDatas = columns
							.stream()
							.map(c -> ColumnData.newOf(c.getId(), dataRepository.selectLastInsertId(),
									targetDatasourceId,
									targetModel.getId()))
							.collect(toList());
					columnDataRepostitory.save(columnDatas.toArray(new ColumnData[] {}));
					/** 3) delete orgin data */
					delete(id, datasourceId);
				});
	}

	@Override
	public void ref(Long id, Long columnId, Long datasourceId, List<Long> datasourceIds) {
		System.out.println(id);
		System.out.println(columnId);
		System.out.println(datasourceId);
		System.out.println(datasourceIds);
	}

	public void submit(Long datasourceId, Long id){
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		String user = ((User)SecurityUtils.getSubject().getPrincipal()).getName();
		dataRepository.submit(model.getTableCode(), datasource.getPassStatus(), id,user );
	}
	
	@Override
	public void pass1(Long datasourceId, Long id) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		String user = ((User)SecurityUtils.getSubject().getPrincipal()).getName();
		dataRepository.pass1(model.getTableCode(), datasource.getPassStatus(), id,user );
	}
	
	@Override
	public void refuse1(Long datasourceId, Long id) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		String user = ((User)SecurityUtils.getSubject().getPrincipal()).getName();
		dataRepository.refuse1(model.getTableCode(),datasource.getPassStatus(), id, user);
		
	}
	
	@Override
	public void pass2(Long datasourceId, Long id) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		String user = ((User)SecurityUtils.getSubject().getPrincipal()).getName();
		dataRepository.pass2(model.getTableCode(),id, user);
		
	}
	
	@Override
	public void refuse2(Long datasourceId, Long id) {
		Datasource datasource = datasourceRepository.find(datasourceId);
		Model model = datasource.getModel();
		String user = ((User)SecurityUtils.getSubject().getPrincipal()).getName();
		dataRepository.refuse2(model.getTableCode(), datasource.getPassStatus(),id, user);
		
	}

	@Override
	public List<Map<String, Object>> findManuscriptFromData(Long serviceId, Long manuId) {
		String tableCode = datasourceRepository.find(serviceId).getModel().getTableCode();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("manu_id", manuId);
		if (serviceId != null) {
			condition.put("datasource_id", serviceId);
		}
		return this.dataRepository.getListData(tableCode, condition);
	}

	private void deleteHeaderPic(Map<String, Object> oldValuesMap) {
		String head = SpringHelper.servletContext.getRealPath("/") + "/resource";
		String tail = oldValuesMap.get("headerPic").toString();

		String path = head + tail;
		File file = new File(path);
		file.delete();

		File fileOfBig = new File(head + new String(tail).replace(".", ("_" + Module.BIG_PIC_WIDTH + ".")));
		fileOfBig.delete();

		File fileOfSmall = new File(head + new String(tail).replace(".", ("_" + Module.SMALL_PIC_WIDTH + ".")));
		fileOfSmall.delete();
	}

}
