package com.dw.suppercms.application.data.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.data.TagService;
import com.dw.suppercms.domain.data.DatasourceRepository;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelRepository;
import com.dw.suppercms.domain.data.Tag;
import com.dw.suppercms.domain.data.TagData;
import com.dw.suppercms.domain.data.TagDataRepository;
import com.dw.suppercms.domain.data.TagRepository;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * TagServiceImpl
 *
 * @author osmos
 * @date 2015年9月8日
 */
@ApplicationService
public class TagServiceImpl implements TagService {

	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private TagDataRepository tagDataRepository;
	@Autowired
	private ModelRepository modelRepository;
	@Autowired
	private DatasourceRepository datasourceRepository;

	@Override
	public Tag create(Tag tag) {
		if (!validateTitle(tag.getTitle())) {
			throw new BusinessException(String.format("重复标签名： %s", tag.getTitle()));
		}

		Tag newTag = Tag.newOf(tag);
		tagRepository.save(newTag);
		return newTag;
	}

	@Override
	public Tag retrieve(Long id) {
		assertNotNull(id);

		Tag tag = tagRepository.find(id);
		boolean success = (tag != null ? true : false);

		if (success) {
			return tag;
		} else {
			throw new BusinessException(String.format("标签在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Tag update(Long id, Tag tag) {
		assertNotNull(id);
		Tag dbTag = retrieve(id).alterOf(tag);
		tagRepository.save(dbTag);
		return dbTag;

	}

	@Override
	public void delete(Long id) {
		assertNotNull(id);
		Tag tag = retrieve(id);

		List<Model> models = modelRepository.findAll();
		for (Model model : models) {
			int count = tagRepository.countByRef(tag, model);
			if (count > 0) {
				throw new BusinessException(String.format("无法删除使用中的标签：id=%s", id));
			}
		}

		tagRepository.remove(tag);
	}

	@Override
	public void dealTags(Long datasourceId, Long dataId, List<Long> tagIds) {
		Long modelId = datasourceRepository.find(datasourceId).getModelId();
		tagDataRepository.deleteTagsOnData(datasourceId, dataId);
		tagIds.stream().map(tagId -> TagData.newOf(tagId, dataId, datasourceId, modelId)).forEach(tagData -> {
			tagDataRepository.save(tagData);
		});
	}

	@Override
	public Boolean validateTitle(String title) {
		return tagRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public SearchResult<Tag> paginateAll(Map<String, Object> condition) {
		return tagRepository.paginateAll(condition);
	}

	@Override
	public List<Tag> all() {
		return tagRepository.findAll();
	}

	@Override
	public List<Tag> tagsOnData(Long datasourceId, Long dataId) {
		return tagDataRepository.tagsOnData(datasourceId, dataId);
	}
}
