package com.dw.suppercms.domain.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dw.suppercms.infrastructure.utils.Pager;
import com.googlecode.genericdao.dao.hibernate.GenericDAO;

/**
 * 
 * DataRepository
 *
 * @author osmos
 * @date 2015年8月13日
 */
public interface DataRepository extends GenericDAO<Data, Long> {

	Long selectLastInsertId();

	/**
	 * get count of records than belong a model table
	 * 
	 * @param tableCode
	 * @return
	 */
	int countModelTable(String tableCode);

	/**
	 * find one data with specified table and id
	 * 
	 * @param tableCode table code name
	 * @param id data id
	 * @return data as a Map
	 */
	Map<String, Object> findDynamicData(String tableCode, Long id);

	/**
	 * save dynamic table data
	 * 
	 * @param model data's model
	 * @param valuesMap saving data
	 */
	void saveDynamicData(Model model, Map<String, Object> valuesMap);

	/**
	 * update dynamic table data
	 * 
	 * @param model data's model
	 * @param oldValuesMap old data state value
	 * @param newValuesMap new data state value
	 */
	void updateDynamicData(Model model, Map<String, Object> oldValuesMap, Map<String, Object> newValuesMap);

	/**
	 * delete dynamic table data
	 * 
	 * @param tableCode table code name
	 * @param id data' id
	 */
	void deleteDynamicData(String tableCode, Long id);

	/**
	 * stick data
	 * 
	 * @param tableCode model's table code
	 * @param id data's id
	 */
	void stick(String tableCode, Long id);

	/**
	 * cancle stick data
	 * 
	 * @param tableCode model's table code
	 * @param id
	 */
	void unstick(String tableCode, Long id);

	void copy(Model sourceModel, Model targetModel,Map<String, Object> valuesMap, Long datasourceId);
	
	void submit(String tableCode,String datasourcePassStatus, Long id, String user);
	void pass1(String tableCode,String datasourcePassStatus, Long id, String user);
	void refuse1(String tableCode,String datasourcePassStatus, Long id, String user);
	void pass2(String tableCode, Long id, String user);
	void refuse2(String tableCode, String datasourcePassStatus,Long id, String user);
	int countOfCheckStatus(String tableCode, List<String> checkStatus);
	
	/**
	 * search data for backend management
	 * 
	 * @param tableCode dynamic table code name
	 * @param datasourceId data's datasourceId
	 * @param condition search condition
	 * @return searched result
	 */
	List<Map<String, Object>> paginateForManage(String tableCode, Long datasourceId, Map<String, Object> condition);

	/**
	 * count data for backend management
	 * 
	 * @param tableCode dynamic table code name
	 * @param datasourceId data's datasourceId
	 * @param condition search condition
	 * @return searched count
	 */
	int countForManage(String tableCode, Long datasourceId, Map<String, Object> condition);
	
	/**
	 * 根据condition条件list数据Map
	 * */
	List<Map<String,Object>> getListData(String tableCode,Map<String,Object> condition);
	
	/**
	 * 数据列表指令检索
	 * */
	List<Map<String,Object>> getListDataToRecordDirective(Model model,Map<String,Object> params,List<Long> columnIds,Date startTime,Date endTime,Pager pager);
	
	/**
	 * 数量列表指令的数量
	 * */
	int getListDataCountToRecordDirective(String tableCode,Map<String,Object> params,List<Long> columnIds,Date startTime,Date endTime);
}
