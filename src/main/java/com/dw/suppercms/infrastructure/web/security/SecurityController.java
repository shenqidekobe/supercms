package com.dw.suppercms.infrastructure.web.security;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.security.MenuService;
import com.dw.suppercms.application.security.RoleService;
import com.dw.suppercms.application.security.UserService;
import com.dw.suppercms.domain.security.Menu;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.Authc;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.Authz;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.Info;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.Permission;
import com.dw.suppercms.infrastructure.web.security.SecurityDto.UserMenus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import flexjson.JSONSerializer;

/**
 * 
 * SecurityController
 *
 * @author osmos
 * @date 2015年9月23日
 */
@RestController
@RequestMapping("/security")
public class SecurityController extends BaseController{
	@Autowired
	private UserService userService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private RoleService roleService;
	@Resource(name = "perms")
	private List<Permission> perms;

	// load user menus
	@RequestMapping(value = "/menus", method = { RequestMethod.GET })
	public String menus() {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		List<Menu> topMenus = topMenus(user);
		UserMenus userMenus = new UserMenus(user.getUsername(), "styles/img/avatars/sunny.png", topMenus);
		String json = new JSONSerializer()
				.include("*.id", "*.uisref", "*.title", "*.icon", "*.lvl", "*.ordinal", "*.parentId", "*.children")
				.exclude("*.class")
				.deepSerialize(userMenus);
		return json;
	}

	// login
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@SystemLog(operation="用户登录",operType=OPER_TYPE.login)
	public Map<String, Info> login(@RequestBody Map<String, Authc> requestBody,HttpServletResponse response) throws Exception {
		UsernamePasswordToken token = new UsernamePasswordToken(
				requestBody.get("token").getPrincipal(),
				requestBody.get("token").getCredentials());
		
		logger.debug("用户开始登录 = "+token.toString());
		token.setRememberMe(true);
		try {
			Subject currentUser = SecurityUtils.getSubject();
			currentUser.login(token);
		} catch (UnknownAccountException e) {
			throw new BusinessException("用户名不存在");
		} catch (AuthenticationException e) {
			throw new BusinessException("密码不正确");
		}

		User user = userService.retriveByUsername(requestBody.get("token").getPrincipal());
		List<String> perms = user.getRole().getPermissions();

		Authc authc = new Authc(token.getUsername(), "********", requestBody.get("token").getRememberme());
		Authz authz = new Authz(Lists.newArrayList(user.getRole().getName()), perms);
		Info info = new Info(authc, authz);

		HashMap<String, Info> infoMap = Maps.newHashMap();
		infoMap.put("info", info);

		requestBodyAsLog(requestBody.get("token").getPrincipal());
		return infoMap;
	}

	// logout
	@RequestMapping(value = "/logout", method = { RequestMethod.POST })
	@SystemLog(operation="用户登出",operType=OPER_TYPE.logout)
	public void logout() {
		requestBodyAsLog(((User)SecurityUtils.getSubject().getPrincipal()).getUsername());
		SecurityUtils.getSubject().logout();
	}

	// load all permissions
	@RequestMapping(value = "/permission", method = { RequestMethod.GET }, params = { "load" })
	public String loadPermission() throws Exception {
		/**
		 * Resource resource = new ClassPathResource("permission.json");
		 * String permission = FileUtils.readFileToString(resource.getFile());
		 */
		List<Menu> menus = menuService.all();
		Map<String, Permission> permissionMap = menus.stream()
				.collect(toMap(Menu::getUisref,
						m -> new Permission(
								m.getTitle(),
								m.getUisref(),
								m.getParent() == null ? null : m.getParent().getUisref(),
								m.getOrdinal(),
								new ArrayList<Permission>())));

		perms.stream().forEach(p -> permissionMap.put(p.getPerm(), p));
		perms.stream().forEach(p -> {
			if(permissionMap.get(p.getParentPerm())!=null){
				permissionMap.get(p.getParentPerm()).getChidldren().add(p);
			}
		});

		menus.stream()
				.filter(m -> m.getParentId() != null)
				.forEach(m -> permissionMap.get(m.getParent().getUisref()).getChidldren()
						.add(permissionMap.get(m.getUisref())));

		permissionMap.values().stream()
				.forEach(p -> p.getChidldren().sort(Comparator.comparing(Permission::getOrdinal)));

		Map<String, Permission> topPermissionMap = permissionMap.values().stream()
				.filter(p -> StringUtils.isEmpty(p.getParentPerm()))
				.collect(toMap(Permission::getPerm, p -> permissionMap.get(p.getPerm())));

		List<Permission> tops = Lists.newArrayList(topPermissionMap.values());
		tops.sort(Comparator.comparing(Permission::getOrdinal));

		String permission = new JSONSerializer().exclude("*.class").deepSerialize(tops);
		return permission;
	}

	private List<Menu> topMenus(User user) {
		user = userService.retrieve(user.getId());
		Long roleId = user.getRoleId();
		List<String> perms = roleService.retrievePermissions(roleId);
		List<Menu> menus = menuService.retrieveMenusByPerms(perms);

		Map<Long, Menu> menuMap = menus.stream().collect(toMap(Menu::getId, menu -> {
			Menu m = Menu.newOf();
			m.setIcon(menu.getIcon());
			m.setLvl(menu.getLvl());
			m.setOrdinal(menu.getOrdinal());
			m.setParentId(menu.getParentId());
			m.setTitle(menu.getTitle());
			m.setUisref(menu.getUisref());
			m.setId(menu.getId());
			return m;
		}));

		menus.stream().filter(s -> s.getParentId() != null).forEach(
				s -> {
					menuMap.get(s.getParent().getId()).getChildren().add(menuMap.get(s.getId()));
				});

		Map<Long, Menu> topMenuMap = menuMap.values().stream()
				.filter(c -> c.getParentId() == null).collect(
						toMap(Menu::getId, c -> menuMap.get(c.getId())));
		return new ArrayList<Menu>(topMenuMap.values());
	}
}
