package com.dw.suppercms.infrastructure.web.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.security.RoleService;
import com.dw.suppercms.domain.security.Role;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * RoleController
 *
 * @author osmos
 * @date 2015年9月17日
 */
@RestController
@RequestMapping("/security")
@SuppressWarnings("unchecked")
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;

	// validate rolename
	@RequestMapping(value = "/roles/validateName", method = { RequestMethod.GET })
	public boolean validateName(Long id, String name) {
		boolean valid = true;
		if (id == null) {
			valid = roleService.validateName(name);
		} else {
			Role role = roleService.retrieve(id);
			if (!role.getName().equals(name)) {
				valid = roleService.validateName(name);
			}
		}
		return valid;
	}

	// retrieve all roles as datatable
	@RequestMapping(value = "/roles", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long sortId, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		SearchResult<Role> data = roleService.paginateAll(conditon);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	// retrieve all roles as list
	@RequestMapping(value = "/roles", method = { RequestMethod.GET })
	public List<Role> all() {
		return roleService.all();
	}

	// retreive a role by id
	@RequestMapping(value = "/roles/{id}", method = { RequestMethod.GET })
	public Role id(@PathVariable Long id) {
		return roleService.retrieve(id);
	}

	// create role
	@RequestMapping(value = "/roles", method = { RequestMethod.POST })
	@SystemLog(operation = "创建角色", operType = OPER_TYPE.create)
	@Description("创建角色")
	@RequiresPermissions({ "app.security.role.create" })
	public Role create(@RequestBody @Valid Role role) {
		Role newRole = roleService.create(role);
		requestBodyAsLog(newRole);
		return newRole;
	}

	// save role
	@RequestMapping(value = "/roles/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑角色", operType = OPER_TYPE.save)
	@Description("修改角色")
	@RequiresPermissions({ "app.security.role.save" })
	public Role save(@RequestBody @Valid Role role, BindingResult br) {
		roleService.update(role.getId(), role);
		requestBodyAsLog(role);
		return role;
	}

	// remove role
	@RequestMapping(value = "/roles/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation = "删除角色", operType = OPER_TYPE.remove)
	@Description("删除角色")
	@RequiresPermissions({ "app.security.role.remove" })
	public void remove(@PathVariable Long id) {
		roleService.delete(id);
	}
	
	// load permissions of role
	@RequestMapping(value = "/roles", method = { RequestMethod.GET }, params = { "permissions" })
	public List<String> getPermissions(Long roleId) {
		return roleService.retrievePermissions(roleId);
	}

	// assign permissions to role
	@RequestMapping(value = "/roles", method = { RequestMethod.POST }, params = { "assignPermissions" })
	//@Description("分配权限")
	//@RequiresPermissions({ "app.security.role", "role.assignPermissions" })
	public void assignPermissions(@RequestBody Map<String, Object> params) {
		Long roleId = Long.parseLong(params.get("roleId").toString());
		List<String> permissions = (List<String>)params.get("permissions");
		roleService.assignPermissions(roleId, permissions);
	}
}
