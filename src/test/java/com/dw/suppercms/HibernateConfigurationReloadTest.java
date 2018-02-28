package com.dw.suppercms;

import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.dw.suppercms.infrastructure.config.TestConfig;

public class HibernateConfigurationReloadTest extends TestConfig {

	@Autowired
	@Qualifier("localSessionFactoryBean")
	LocalSessionFactoryBean localSessionFactoryBean;
	@Autowired
	@Qualifier("sessionFactory")
	SessionFactory factory;

	//@Test
	public void test() throws Exception {
		Configuration configuration = localSessionFactoryBean.getConfiguration();
		Resource resource = new ClassPathResource("com/dw/suppercms/News.hbm.xml");
		configuration.addFile(resource.getFile());
		configuration.buildMappings();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		factory = configuration.buildSessionFactory(serviceRegistry);
		factory.openSession().createQuery("from News");
	}
	
	@Test
	public void test1() {
		SQLQuery query = factory.openSession().createSQLQuery("select * from news");
		query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		Map map = (Map)query.uniqueResult();
		System.out.println(map.get("title"));
	}
}
