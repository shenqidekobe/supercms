package com.dw.suppercms.application.modules.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import lombok.Setter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.modules.CustomService;
import com.dw.suppercms.application.modules.impl.ModuleEvent.ModuleEventType;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.CustomRepository;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * CustomServiceImpl
 *
 * @author osmos
 * @date 2015年7月14日
 */
@ApplicationService
public class CustomServiceImpl
		implements CustomService, ApplicationEventPublisherAware {

	@Autowired
	private CustomRepository customRepositry;
	@Autowired
	private SysSortRepository sortRepository;

	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return customRepositry.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Boolean validateLocation(String location) {
		return customRepositry.countByLocation(location) == 0 ? true : false;
	}

	@Override
	public Custom create(Custom custom) {

		Custom newCustom = Custom.newOf(custom);
		assertTrue("名字重复", validateTitle(custom.getTitle()));
		assertTrue("文件路径重复", validateLocation(custom.getLocation()));
		customRepositry.save(newCustom);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.CREATE_CUSTOM, newCustom);
		applicationEventPublisher.publishEvent(moduleEvent);

		return newCustom;

	}

	@Override
	public Custom retrieve(Long id) {

		assertNotNull(id);

		Custom custom = customRepositry.find(id);
		boolean success = (custom != null ? true : false);

		if (success) {
			return custom;
		} else {
			throw new BusinessException(String.format("自定义模块在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Custom update(Long id, Custom newCustom) {

		assertNotNull(id);

		Custom dbCustom = retrieve(id);
		if (!Objects.equals(dbCustom.getTitle(), newCustom.getTitle())) {
			assertTrue("名字重复", validateTitle(newCustom.getTitle()));
		}
		if (!Objects.equals(dbCustom.getLocation(), newCustom.getLocation())) {
			assertTrue("文件路径重复", validateLocation(newCustom.getLocation()));
		}

		Custom oldCustom = Custom.newOf();
		BeanUtils.copyProperties(dbCustom, oldCustom);

		customRepositry.save(dbCustom.alterOf(newCustom));

		Object[] data = new Object[] { oldCustom, newCustom };
		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.UPDATE_CUSTOM, data);
		applicationEventPublisher.publishEvent(moduleEvent);

		return dbCustom;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Custom oldCustom = retrieve(id);
		customRepositry.remove(oldCustom);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.DELETE_CUSTOM, oldCustom);
		applicationEventPublisher.publishEvent(moduleEvent);
		return id;
	}

	@Override
	public SearchResult<Custom> paginateAll(Map<String, Object> condition, int startIndex, int maxResults,
			List<Sort> sorts) {
		Function<SysSortInfo, List<Long>> fn = new Function<SysSortInfo, List<Long>>() {
			@Override
			public List<Long> apply(SysSortInfo t) {
				List<Long> list = new ArrayList<Long>();
				list.add(t.getId());
				if (t.getChildren().size() != 0) {
					for (SysSortInfo sort : t.getChildren()) {
						list.addAll(this.apply(sort));
					}
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
		return customRepositry.paginateAll(condition, startIndex, maxResults, sorts);
	}
	
	@Override
	public List<Custom> findCustomListBySortId(Long sortId){
		Search search=new Search();
		if(sortId!=null){
			search.addFilterEqual("sortId", sortId);
		}
		return this.customRepositry.search(search);
	}
}
