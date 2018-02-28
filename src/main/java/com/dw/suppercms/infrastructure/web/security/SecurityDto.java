package com.dw.suppercms.infrastructure.web.security;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.dw.suppercms.domain.security.Menu;

public class SecurityDto {

	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Info {
		@Getter
		@Setter
		private Authc authc;
		@Getter
		@Setter
		private Authz authz;
	}

	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Authc {
		@Getter
		@Setter
		private String principal;
		@Getter
		@Setter
		private String credentials;
		@Getter
		@Setter
		private boolean rememberme;
	}

	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Authz {
		@Getter
		@Setter
		private List<String> roles;
		@Getter
		@Setter
		private List<String> permissions;
	}
	
	
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class UserMenus {
		@Getter
		@Setter
		private String username;
		@Getter
		@Setter
		private String picture;
		@Getter
		@Setter
		private List<Menu> menus;
	}
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Permission {
		@Getter
		@Setter
		private String title;
		@Getter
		@Setter
		private String perm;
		@Getter
		@Setter
		private String parentPerm;
		@Getter
		@Setter
		private float ordinal;
		@Getter
		@Setter
		private List<Permission> chidldren = new ArrayList<Permission>();
		
	}
	
	
	
}
