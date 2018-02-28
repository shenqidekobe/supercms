package com.dw.suppercms.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.dw.framework.core.config.CoreAppConfig;
import com.dw.framework.core.config.GenericAppConfig;
import com.dw.suppercms.application.Startup;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.Permission;

@Configuration
@Import(CoreAppConfig.class)
public class AppConfig implements GenericAppConfig {

	@Bean(name = "perms")
	public List<Permission> perms() {
		List<Permission> perms = new ArrayList<Permission>();
		return perms;
	}

	@Bean
	public Startup startup() {
		return new Startup();
	}

}