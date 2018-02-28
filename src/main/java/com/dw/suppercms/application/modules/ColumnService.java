package com.dw.suppercms.application.modules;

import java.util.List;

import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.modules.Column;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ColumnService
 *
 * @author osmos
 * @date 2015年7月2日
 */
public interface ColumnService {

	/**
	 * validate title when creating or updating column
	 * 
	 * @param title column's title
	 * @return true if valid, false if invalid
	 */
	public Boolean validateTitle(String title);

	/**
	 * validate directory name when creating or updating column
	 * 
	 * @param dirWebpath column's dirWebpath
	 * @return true if valid, false if invalid
	 */
	public Boolean validateDirName(String dirName);

	/**
	 * create a new column
	 * 
	 * @param column holds the new column state
	 * @return the saved column
	 */
	Column create(Column column);

	/**
	 * retrieve a column
	 * 
	 * @param id column's id
	 * @return the retrieved column
	 */
	Column retrieve(Long id);

	/**
	 * retrieve columnDatas
	 * 
	 * @param ids some data id
	 * @return matched columnDatas
	 */
	List<ColumnData> retrieveByDataIds(Long[] ids);

	/**
	 * update a column
	 * 
	 * @param id the id of the updating column
	 * @param newcolumn holds the new column state
	 * @return the updated column
	 */
	Column update(Long id, Column newcolumn);
	
	/**
	 * update column makeListState
	 * */
	Column update(Long id, boolean makeListState);


	/**
	 * delete a column
	 * 
	 * @param id the id of the deleting column
	 * @return the deleted column's id
	 */
	Long delete(Long id);

	/**
	 * retrieve columns by site
	 * 
	 * @param siteId column's siteId
	 * @param startIndex get from start index
	 * @param maxResults get how many
	 * @return retrieved columns
	 */
	SearchResult<Column> retrieveBySite(Long siteId, int startIndex, int maxResults);

	/**
	 * retrieve all columns
	 * 
	 * @return all columns
	 */
	List<Column> all();
	
	List<Column> own();
	
	/**
	 * 查询最顶级的栏目
	 * */
	List<Column> findRootColumn(Long siteId);

}
