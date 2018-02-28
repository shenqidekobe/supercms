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
import com.dw.suppercms.application.modules.SiteService;
import com.dw.suppercms.application.modules.impl.ModuleEvent.ModuleEventType;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.modules.SiteRepository;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * SiteServiceImpl
 *
 * @author osmos
 * @date 2015年6月15日
 */
@ApplicationService
public class SiteServiceImpl
		implements SiteService, ApplicationEventPublisherAware {

	@Autowired
	private SiteRepository siteRepository;
	@Setter
	private ApplicationEventPublisher applicationEventPublisher;

	// interface implements

	@Override
	public Boolean validateTitle(String title) {
		return siteRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Boolean validateTestDomain(String testDomain) {
		return siteRepository.countByTestDomain(testDomain) == 0 ? true : false;
	}

	@Override
	public Boolean validateProductDomain(String productDomain) {
		return siteRepository.countByProductDomain(productDomain) == 0 ? true : false;
	}

	@Override
	public Boolean validateDirName(String dirName) {
		return siteRepository.countByDirName(dirName) == 0 ? true : false;
	}

	@Override
	public Site create(Site site) {

		Site newSite = Site.newOf(site);
		siteRepository.save(newSite);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.CREATE_SITE, newSite);
		applicationEventPublisher.publishEvent(moduleEvent);

		return newSite;

	}

	@Override
	public Site retrieve(Long id) {

		assertNotNull(id);

		Site site = siteRepository.find(id);
		boolean success = (site != null ? true : false);

		if (success) {
			return site;
		} else {
			throw new BusinessException(String.format("站点在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Site update(Long id, Site newSite) {

		assertNotNull("id must not be null", id);

		Site dbSite = retrieve(id);
		Site oldSite = Site.newOf();
		BeanUtils.copyProperties(dbSite, oldSite);

		siteRepository.save(dbSite.alterOf(newSite));

		Object[] data = new Object[] { oldSite, newSite };
		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.UPDATE_SITE, data);
		applicationEventPublisher.publishEvent(moduleEvent);

		return dbSite;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Site oldSite = retrieve(id);
		siteRepository.remove(oldSite);

		ModuleEvent moduleEvent = new ModuleEvent(ModuleEventType.DELETE_SITE, oldSite);
		applicationEventPublisher.publishEvent(moduleEvent);
		return id;
	}

	@Override
	public List<Site> all() {
		return siteRepository.findAll();
	}

	@Override
	public SearchResult<Site> paginateAll(int startIndex, int maxResults) {
		return siteRepository.paginateAll(startIndex, maxResults);
	}
}
