package com.dw.suppercms.infrastructure.config;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

import com.dw.framework.core.config.CoreTestConfig;

@ContextHierarchy({ @ContextConfiguration(classes = AppConfig.class),
		@ContextConfiguration(classes = WebConfig.class) })
public abstract class TestConfig extends CoreTestConfig {

}
