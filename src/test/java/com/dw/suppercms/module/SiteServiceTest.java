package com.dw.suppercms.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.SpringHelper;
import com.dw.suppercms.application.modules.SiteService;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.infrastructure.config.TestConfig;

/**
 * SiteServiceTest
 *
 * @author osmos
 * @date 2015年6月12日
 */
public class SiteServiceTest extends TestConfig {

	@Autowired
	private SiteService siteService;
	private Site site;

	@Before
	public void before() {
		Site site = Site.newOf();
		site.setTitle("中直");
		site.setDescription("this is description");
		site.setDirName("/cpscp");
		site.setProductDomain("www.cpscp.product.com");
		site.setTestDomain("www.cpscp.test.com");
		this.site = site;
	}

	@Test
	public void createSite() {
		siteService.create(site);
		String rootRealPath = SpringHelper.servletContext.getRealPath("/");
		assertEquals(site.getTitle(), "中直");
		assertEquals(site.getDescription(), "this is description");
		assertEquals(site.getProductDomain(), "www.cpscp.product.com");
		assertEquals(site.getTestDomain(), "www.cpscp.test.com");
		assertEquals(site.getExtensionName(), "html");
		assertEquals(site.getFileName(), "index");
		assertEquals(site.getModuleType(), "SITE");
		assertEquals(site.getDirName(), "/cpscp");
		assertEquals(site.getDirWebpath(), "/");
		assertEquals(site.getDirDiskpath(), rootRealPath + Module.MAKE_DIR + "/cpscp");
		assertEquals(site.getFileWebpath(0), "/index.html");
		assertEquals(site.getFileDiskpath(0), rootRealPath + Module.MAKE_DIR + "/cpscp/index.html");
		assertTrue(new File(site.getDirDiskpath()).exists());
	}

}
