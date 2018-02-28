package com.dw.suppercms.application.modules.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import lombok.Setter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.modules.ColumnService;
import com.dw.suppercms.application.modules.impl.ModuleEvent.ModuleEventType;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.data.ColumnDataRepostitory;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.ColumnRepository;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ColumnServiceImpl
 *
 * @author osmos
 * @date 2015年6月15日
 */
@ApplicationService
public class ColumnServiceImpl
		implements ColumnService, ApplicationEventPublisherAware {

	@Autowired
	private ColumnRepository columnRepositry;
	@Autowired
	private DatasourceRepository datasourceRepository;
	@Autowired
	private ColumnDataRepostitory columnDataRepostitory;
	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return columnRepositry.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Boolean validateDirName(String dirName) {
		return columnRepositry.countByDirName(dirName) == 0 ? true : false;
	}

	@Override
	public Column create(Column column) {

		Column newColumn = Column.newOf(column);
		columnRepositry.save(newColumn);

		String tableCode = newColumn.getDatasource().getModel().getTableCode();
		Long modelId = newColumn.getDatasource().getModelId();
		Long datasourceId = newColumn.getDatasourceId();
		List<Long> dataIds = columnDataRepostitory.findDataIds(tableCode, datasourceId);
		columnDataRepostitory.addColumnData(newColumn.getId(), modelId, datasourceId, dataIds);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.CREATE_COLUMN, newColumn);
		applicationEventPublisher.publishEvent(moduleEvent);

		return newColumn;

	}

	@Override
	public Column retrieve(Long id) {

		assertNotNull(id);

		Column column = columnRepositry.find(id);
		boolean success = (column != null ? true : false);

		if (success) {
			return column;
		} else {
			throw new BusinessException(String.format("站点在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Column update(Long id, Column newColumn) {

		assertNotNull(id);

		Column dbColumn = retrieve(id);

		if (dbColumn.getId().equals(newColumn.getParentId())) {
			return dbColumn;
		}

		if (newColumn.getParentId() != null) {
			Column parent = columnRepositry.find(newColumn.getParentId());
			if (dbColumn.getId() == parent.getParentId()) {
				return dbColumn;
			}
		}

		Column oldColumn = Column.newOf();
		BeanUtils.copyProperties(dbColumn, oldColumn);

		columnRepositry.save(dbColumn.alterOf(newColumn));

		if (!oldColumn.getDatasourceId().equals(newColumn.getDatasourceId())) {
			columnDataRepostitory.deleteColumnData(id, oldColumn.getDatasourceId());
			Datasource newDatasource = datasourceRepository.find(newColumn.getDatasourceId());
			List<Long> dataIds = columnDataRepostitory.findDataIds(newDatasource.getModel()
					.getTableCode(), newColumn.getDatasourceId());
			columnDataRepostitory.addColumnData(id, newDatasource.getModelId(), newDatasource.getId(), dataIds);
		}

		Object[] data = new Object[] { oldColumn, newColumn };
		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.UPDATE_COLUMN, data);
		applicationEventPublisher.publishEvent(moduleEvent);

		return dbColumn;
	}

	@Override
	public Column update(Long id, boolean makeListState) {
		assertNotNull(id);

		Column dbColumn = retrieve(id);
		dbColumn.setMakeListState(makeListState);
		columnRepositry.save(dbColumn);

		return dbColumn;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Column oldColumn = retrieve(id);
		columnRepositry.remove(oldColumn);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.DELETE_COLUMN, oldColumn);
		applicationEventPublisher.publishEvent(moduleEvent);
		return id;
	}

	@Override
	public SearchResult<Column> retrieveBySite(Long siteId, int startIndex, int maxResults) {
		return columnRepositry.retrieveBySite(siteId, startIndex, maxResults);
	}

	@Override
	public List<Column> all() {
		return columnRepositry.findAll();
	}

	@Override
	public List<Column> own() {
		return columnRepositry.ownColumn();
	}

	@Override
	public List<ColumnData> retrieveByDataIds(Long[] ids) {
		return columnDataRepostitory.retrieveColumnsByDataIds(ids);
	}

	@Override
	public List<Column> findRootColumn(Long siteId) {
		return columnRepositry.search(new Search().addFilterNull("parentId").addFilterEqual("siteId", siteId));
	}
}
