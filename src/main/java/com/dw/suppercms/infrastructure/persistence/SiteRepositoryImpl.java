package com.dw.suppercms.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.modules.SiteRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

@Repository
public class SiteRepositoryImpl
		extends GenericRepositoryImpl<Site, Long>
		implements SiteRepository {

	public static final String COUNT_BY_TITLE = "select count(*) from Site where title = :title";
	public static final String COUNT_BY_TEST_DOMAIN = "select count(*) from Site where testDomain = :testDomain";
	public static final String COUNT_BY_PRODUCT_DOMAIN = "select count(*) from Site where productDomain = :productDomain";
	public static final String COUNT_BY_DIR_NAME = "select count(*) from Site where dirName = :dirName";

	@Override
	public int countByTitle(String title) {
		Object result = getSession().createQuery(COUNT_BY_TITLE)
				.setParameter("title", title)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByTestDomain(String testDomain) {
		Object result = getSession().createQuery(COUNT_BY_TEST_DOMAIN)
				.setParameter("testDomain", testDomain)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByProductDomain(String productDomain) {
		Object result = getSession().createQuery(COUNT_BY_PRODUCT_DOMAIN)
				.setParameter("productDomain", productDomain)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public int countByDirName(String dirName) {
		Object result = getSession().createQuery(COUNT_BY_DIR_NAME)
				.setParameter("dirName", dirName)
				.uniqueResult();
		return ((Long) result).intValue();
	}

	@Override
	public SearchResult<Site> paginateAll(int startIndex, int maxResults) {
		Search search = new Search(Site.class);
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("createTime", false)));
		return searchAndCount(search);
	}
}
