package com.dw.suppercms.application.security.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.security.RoleService;
import com.dw.suppercms.domain.security.Role;
import com.dw.suppercms.domain.security.RoleRepository;
import com.dw.suppercms.domain.security.UserRepository;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * RoleServiceImpl
 *
 * @author osmos
 * @date 2015年9月17日
 */
@ApplicationService
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Boolean validateName(String name) {
		return roleRepository.countByName(name) == 0 ? true : false;
	}

	@Override
	public Role create(Role role) {
		Role newRole = Role.newOf(role);
		roleRepository.save(newRole);
		return newRole;

	}

	@Override
	public Role retrieve(Long id) {

		assertNotNull(id);

		Role role = roleRepository.find(id);
		boolean success = (role != null ? true : false);

		if (success) {
			return role;
		} else {
			throw new BusinessException(String.format("角色在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Role update(Long id, Role newRole) {

		assertNotNull(id);

		Role dbRole = retrieve(id);
		roleRepository.save(dbRole.alterOf(newRole));

		return dbRole;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		Role oldRole = retrieve(id);

		boolean canDelete = userRepository.countByRoleId(id) == 0 ? true : false;
		if (!canDelete) {
			throw new BusinessException("无法删除已分配了用户的角色！");
		} else {
			roleRepository.remove(oldRole);
		}
		return id;
	}

	
	@Override
	public List<Role> all() {
		return roleRepository.findAll();
	}

	@Override
	public SearchResult<Role> paginateAll(Map<String, Object> condition) {
		return roleRepository.paginateAll(condition);
	}

	@Override
	public List<String> retrievePermissions(Long roleId) {
		return roleRepository.getPermissions(roleId);
	}
	
	@Override
	public void assignPermissions(Long roleId, List<String> permissions) {
		roleRepository.clearPermission(roleId);
		roleRepository.assignPermission(roleId, permissions);
	}
}
