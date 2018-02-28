package com.dw.suppercms.application.data.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Setter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.ModelService;
import com.dw.suppercms.application.data.impl.SchemalEvent.SchemalEventType;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelFieldRepository;
import com.dw.suppercms.domain.data.ModelRepository;
import com.google.common.collect.Maps;
import com.googlecode.genericdao.search.SearchResult;

/**
 * ModelServiceImpl
 *
 * @author osmos
 * @date 2015年7月31日
 */
@ApplicationService
public class ModelServiceImpl
		implements ModelService, ApplicationEventPublisherAware {

	@Autowired
	private ModelRepository modelRepository;
	@Autowired
	private ModelFieldRepository modelFieldRepository;
	@Autowired
	DataRepository dataRepository;
	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return modelRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Boolean validateTableCode(String tableCode) {
		return modelRepository.countByTableCode(tableCode) == 0 ? true : false;
	}

	@Override
	public Model create(Model model) {

		if (!validateTitle(model.getTitle())) {
			throw new BusinessException(String.format("重复模型名： %s", model.getTitle()));
		}
		if (!validateTableCode(model.getTableCode())) {
			throw new BusinessException(String.format("重复模型表名： %s", model.getTableCode()));
		}

		Model newModel = Model.newOf(model);
		modelRepository.save(newModel);

		modelFieldRepository.save(ModelField.newDefaults(newModel.getId()).toArray(new ModelField[] {}));

		Map<String, Object> data = Maps.newHashMap();
		data.put("model", newModel);
		SchemalEvent schemalEvent = new SchemalEvent(this, SchemalEventType.CREATE_TABLE, data);
		applicationEventPublisher.publishEvent(schemalEvent);

		return newModel;

	}

	@Override
	public Model retrieve(Long id) {

		assertNotNull(id);

		Model model = modelRepository.find(id);
		boolean success = (model != null ? true : false);

		if (success) {
			return model;
		} else {
			throw new BusinessException(String.format("模型在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Model update(Long id, Model newModel) {

		assertNotNull(id);

		Model dbModel = retrieve(id);
		Model oldModel = Model.newOf();
		BeanUtils.copyProperties(dbModel, oldModel);

		modelRepository.save(dbModel.alterOf(newModel));

		if (!Objects.equals(oldModel.getTableCode(), newModel.getTableCode())) {
			Map<String, Object> data = Maps.newHashMap();
			data.put("oldModel", oldModel);
			data.put("newModel", newModel);
			applicationEventPublisher.publishEvent(new SchemalEvent(this, SchemalEventType.RENAME_TABLE, data));
		}

		return dbModel;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Model model = retrieve(id);

		int count = dataRepository.countModelTable(model.getTableCode());
		if (count != 0) {
			throw new BusinessException("模型表包含有数据，无法删除");
		}
		modelFieldRepository.removeFieldsOfModel(model.getId());
		modelRepository.remove(model);

		Map<String, Object> data = Maps.newHashMap();
		data.put("model", model);
		applicationEventPublisher.publishEvent(new SchemalEvent(this, SchemalEventType.DELETE_TABLE, data));

		return id;
	}

	@Override
	public List<Model> all() {
		return modelRepository.findAll();
	}

	@Override
	public SearchResult<Model> paginateAll(Map<String, Object> condition) {
		return modelRepository.paginateAll(condition);
	}

}
