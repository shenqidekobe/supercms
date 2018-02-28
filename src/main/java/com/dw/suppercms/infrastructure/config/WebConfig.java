package com.dw.suppercms.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.dw.framework.core.config.CoreWebConfig;
import com.dw.framework.core.config.GenericWebConfig;

@Configuration
@Import(CoreWebConfig.class)
public class WebConfig implements GenericWebConfig {

}
