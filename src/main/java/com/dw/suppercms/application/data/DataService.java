package com.dw.suppercms.application.data;

import java.util.List;
import java.util.Map;

/**
 * 
 * DataService
 *
 * @author osmos
 * @date 2015年8月12日
 */
public interface DataService {

	/**
	 * search data for backend management
	 * 
	 * @param datasourceId data's datasource id
	 * @param condition searcch condition
	 * @return searched result
	 */
	List<Map<String, Object>> paginateForManage(Long datasourceId, Map<String, Object> condition);

	/**
	 * cocunt data for backend management
	 * 
	 * @param datasourceId data's datasource id
	 * @param condition searcch condition
	 * @return searched count
	 */
	int countForManage(Long datasourceId, Map<String, Object> condition);

	/**
	 * add a data for datasource
	 * 
	 * @param data holds the creating data state as map
	 */
	void create(Map<String, Object> data);

	/**
	 * retrieve a data record
	 * 
	 * @param datasourceId datasource's id
	 * @param id data's id
	 * @return retrieved data
	 */
	Map<String, Object> retrieve(Long datasourceId, Long id);

	/**
	 * update a data
	 * 
	 * @param data holds the updating data state as map
	 */
	void update(Map<String, Object> data);

	/**
	 * delete a data
	 * 
	 * @param id data's id
	 * @param datasourceId data's datasource id
	 */
	void delete(Long id, Long datasourceId);

	/**
	 * stick data
	 * 
	 * @param id data's id
	 * @param datasourceId data's datasource id
	 */
	void stick(Long id, Long datasourceId);

	/**
	 * cancle stick data
	 * 
	 * @param id
	 * @param datasourceId data's datasource id
	 */
	void unstick(Long id, Long datasourceId);

	/**
	 * copy data to datasources
	 * 
	 * @param id data's id
	 * @param datasourceId data's datasourceId
	 * @param datasourceIds target datasources
	 */
	void copy(Long id, Long datasourceId, List<Long> datasourceIds);

	/**
	 * move data to datasources
	 * 
	 * @param id data's id
	 * @param datasourceId data's datasourceId
	 * @param datasourceIds target datasources
	 */
	void move(Long id, Long datasourceId, List<Long> datasourceIds);

	/**
	 * ref data to datasources
	 * 
	 * @param id data's id
	 * @param columnId data's columnId
	 * @param datasourceId data's datasourceId
	 * @param datasourceIds target datasources
	 */
	void ref(Long id, Long columnId, Long datasourceId, List<Long> datasourceIds);

	void submit(Long datasourceId, Long id);
	void pass1(Long datasourceId, Long id);
	void refuse1(Long datasourceId, Long id);
	void pass2(Long datasourceId, Long id);
	void refuse2(Long datasourceId, Long id);
	
	/**
	 * 获取稿件检录从数据表中
	 * */
	List<Map<String, Object>> findManuscriptFromData(Long serviceId,Long manuId);

}
