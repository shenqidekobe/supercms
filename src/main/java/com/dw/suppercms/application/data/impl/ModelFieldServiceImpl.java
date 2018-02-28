package com.dw.suppercms.application.data.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.Objects;

import lombok.Setter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.ModelFieldService;
import com.dw.suppercms.application.data.ModelService;
import com.dw.suppercms.application.data.impl.SchemalEvent.SchemalEventType;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelFieldRepository;
import com.google.common.collect.Maps;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * ModelFieldServiceImpl
 *
 * @author osmos
 * @date 2015年8月3日
 */
@ApplicationService
public class ModelFieldServiceImpl
		implements ModelFieldService, ApplicationEventPublisherAware {

	@Autowired
	private ModelService modelService;
	@Autowired
	private DatasourceRepository datasourceRepository;
	@Autowired
	private ModelFieldRepository modelFieldRepository;
	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return modelFieldRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Boolean validateFieldCode(String fieldCode) {
		return modelFieldRepository.countByFieldCode(fieldCode) == 0 ? true : false;
	}

	@Override
	public ModelField create(ModelField field) {
		if (!validateTitle(field.getTitle())) {
			throw new BusinessException(String.format("重复模型字段名： %s", field.getTitle()));
		}
		if (!validateFieldCode(field.getFieldCode())) {
			throw new BusinessException(String.format("重复表字段名： %s", field.getFieldCode()));
		}

		ModelField newField = ModelField.newOf(field);
		modelFieldRepository.save(newField);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("model", modelService.retrieve(field.getModelId()));
		data.put("field", newField);
		SchemalEvent schemalEvent = new SchemalEvent(this, SchemalEventType.CREATE_FIELD, data);
		applicationEventPublisher.publishEvent(schemalEvent);

		return newField;
	}

	@Override
	public ModelField retrieve(Long id) {

		assertNotNull(id);

		ModelField modelField = modelFieldRepository.find(id);
		boolean success = (modelField != null ? true : false);

		if (success) {
			return modelField;
		} else {
			throw new BusinessException(String.format("模型字段在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public ModelField update(Long id, ModelField newField) {

		assertNotNull(id);

		ModelField dbField = retrieve(id);
		ModelField oldField = ModelField.newOf();
		BeanUtils.copyProperties(dbField, oldField);

		modelFieldRepository.save(dbField.alterOf(newField));

		if (!Objects.equals(oldField.getFieldCode(), newField.getFieldCode())
				|| !Objects.equals(oldField.getDataType(), newField.getDataType())
				|| !Objects.equals(oldField.getDataLength(), newField.getDataLength())) {
			Map<String, Object> data = Maps.newHashMap();
			data.put("model", modelService.retrieve(dbField.getModelId()));
			data.put("oldField", oldField);
			data.put("newField", newField);
			applicationEventPublisher.publishEvent(new SchemalEvent(this, SchemalEventType.ALTER_FIELD, data));
		}

		return dbField;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		ModelField dbField = retrieve(id);
		modelFieldRepository.remove(dbField);

		Map<String, Object> data = Maps.newHashMap();
		data.put("model", dbField.getModel());
		data.put("field", dbField);
		applicationEventPublisher.publishEvent(new SchemalEvent(this, SchemalEventType.DELETE_FIELD, data));

		return id;
	}

	@Override
	public SearchResult<ModelField> paginateAll(Map<String, Object> condition) {
		return modelFieldRepository.paginateAll(condition);
	}

}
