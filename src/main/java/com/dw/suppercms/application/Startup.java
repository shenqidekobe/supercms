package com.dw.suppercms.application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Async;

import com.dw.suppercms.infrastructure.web.security.SecurityDto.Permission;

public class Startup {

	@Resource(name = "perms")
	private List<Permission> perms;

	@PostConstruct
	@Async
	public void startup() {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("com.dw.suppercms.infrastructure.web"))
				.setScanners(new MethodAnnotationsScanner()));
		Set<Method> methods = reflections.getMethodsAnnotatedWith(RequiresPermissions.class);
		methods.stream().forEach(new Consumer<Method>() {
			@Override
			public void accept(Method m) {
				Description description = m.getAnnotation(Description.class);
				String desc = description == null ? "未知" : description.value();
				RequiresPermissions requiresPermissions = m.getAnnotation(RequiresPermissions.class);
				String[] values = requiresPermissions.value();

				for (int i = 0; i < values.length; i++) {
					String perm = values[i];
					String menuPerm = StringUtils.substringBeforeLast(perm, ".");
					perms.add(new Permission(desc, perm, menuPerm, 0f, new ArrayList<Permission>()));
				}
			}
		});
	}
}
