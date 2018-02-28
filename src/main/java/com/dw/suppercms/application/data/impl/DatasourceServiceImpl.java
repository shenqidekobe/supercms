package com.dw.suppercms.application.data.impl;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.DatasourceService;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.data.Datasource.PassStatus;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.data.ModelRepository;
import com.dw.suppercms.domain.security.UserDataSource;
import com.dw.suppercms.domain.security.UserDataSourceRepository;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * DatasourceServiceImpl
 *
 * @author osmos
 * @date 2015年7月28日
 */
@ApplicationService
public class DatasourceServiceImpl
		implements DatasourceService {

	@Autowired
	private DatasourceRepository datasourceRepository;
	@Autowired
	private SysSortRepository sortRepository;
	@Autowired
	private UserDataSourceRepository userDataSourceRepository;
	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private ModelRepository modelRepository;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return datasourceRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Datasource create(Datasource datasource) {

		Datasource newDatasource = Datasource.newOf(datasource);
		datasourceRepository.save(newDatasource);

		return newDatasource;

	}

	@Override
	public Datasource retrieve(Long id) {

		assertNotNull(id);

		Datasource datasource = datasourceRepository.find(id);
		boolean success = (datasource != null ? true : false);

		if (success) {
			return datasource;
		} else {
			throw new BusinessException(String.format("数据源在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Datasource update(Long id, Datasource newDatasource) {

		assertNotNull(id);

		Datasource dbDatasource = retrieve(id);
		String oldPassStatus = dbDatasource.getPassStatus();
		String newPassStatus = newDatasource.getPassStatus();
		if(!oldPassStatus.equals(newPassStatus)){
			String tableCode = modelRepository.find(newDatasource.getModelId()).getTableCode();
			if(oldPassStatus.equals(PassStatus.TWO.name())){
				if(newPassStatus.equals(PassStatus.ONE.name())){
					if(dataRepository.countOfCheckStatus(tableCode, Lists.newArrayList("SECOND")) != 0){
						throw new BusinessException("数据源包含未处理的数据，请处理后再修改");
					}
				}
				if(newPassStatus.equals(PassStatus.NO.name())){
					if(dataRepository.countOfCheckStatus(tableCode, Lists.newArrayList("ORIGIN", "FIRST_REFUSE", "FIRST", "SECOND_REFUSE", "SECOND")) != 0){
						throw new BusinessException("数据源包含未处理的数据，请处理后再修改");
					}
				}
			}
			if(oldPassStatus.equals(PassStatus.ONE.name())){
				if(newPassStatus.equals(PassStatus.NO.name())){
					if(dataRepository.countOfCheckStatus(tableCode, Lists.newArrayList("ORIGIN", "FIRST_REFUSE", "FIRST", "SECOND_REFUSE", "SECOND")) != 0){
						throw new BusinessException("数据源包含未处理的数据，请处理后再修改");
					}
				}
			}
		}
		datasourceRepository.save(dbDatasource.alterOf(newDatasource));

		return dbDatasource;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Datasource oldDatasource = retrieve(id);
		datasourceRepository.remove(oldDatasource);

		return id;
	}

	@Override
	public List<Datasource> all() {
		return datasourceRepository.findAll();
	}

	@Override
	public SearchResult<Datasource> paginateAll(Map<String, Object> condition) {
		Function<SysSortInfo, List<Long>> fn = new Function<SysSortInfo, List<Long>>() {
			@Override
			public List<Long> apply(SysSortInfo t) {
				List<Long> list = new ArrayList<Long>();
				list.add(t.getId());
				if (t.getChildren().size() != 0) {
					t.getChildren().parallelStream().forEach(s -> list.addAll(this.apply(s)));
				}
				return list;
			}
		};
		if (condition.containsKey("sortId") && condition.get("sortId") != null) {
			Long sortId = (Long) condition.get("sortId");
			List<Long> sortIds = Lists.newArrayList(sortId);
			sortRepository.find(sortId).getChildren().parallelStream().forEach(s -> sortIds.addAll(fn.apply(s)));
			condition.remove("sortId");
			condition.put("sortIds", sortIds);
		}
		return datasourceRepository.paginateAll(condition);
	}

	@Override
	public List<Long> retrieveDatasourceIds(Long userId) {
		return datasourceRepository.getDatasources(userId).stream().map(d -> d.getId()).collect(toList());
	}
	
	@Override
	public List<Datasource> retrieveDatasources(Long userId) {
		return datasourceRepository.getDatasources(userId);
	}

	@Override
	public void assignDatasources(Long userId, List<Long> datasourceIds) {
		datasourceRepository.clearDatasources(userId);
		datasourceIds.stream()
				.forEach(datasourceId -> userDataSourceRepository.save(UserDataSource.newOf(userId, datasourceId)));
	}
}
