package com.dw.suppercms.application.security.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.security.MenuService;
import com.dw.suppercms.domain.security.Menu;
import com.dw.suppercms.domain.security.MenuRepository;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * MenuServiceImpl
 *
 * @author osmos
 * @date 2015年9月24日
 */
@ApplicationService
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepository;

	@Override
	public Boolean validateTitle(String title) {
		return menuRepository.countByTitle(title) == 0 ? true : false;
	}

	@Override
	public Menu create(Menu menu) {
		Menu newMenu = Menu.newOf(menu);
		if(newMenu.getParentId() == null){
			newMenu.setLvl(1);
		}else{
			Menu parent = retrieve(newMenu.getParentId());
			newMenu.setLvl(parent.getLvl()+1);
		}
		menuRepository.save(newMenu);
		return newMenu;
	}

	@Override
	public Menu retrieve(Long id) {
		Menu menu = menuRepository.find(id);
		boolean success = (menu != null ? true : false);
		if (success) {
			return menu;
		} else {
			throw new BusinessException(String.format("菜单在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public Menu update(Long id, Menu newMenu) {
		assertNotNull(id);

		Menu dbMenu = retrieve(id);

		if (dbMenu.getId().equals(newMenu.getParentId())) {
			return dbMenu;
		}
		
		if (newMenu.getParentId() != null) {
			Menu parent = menuRepository.find(newMenu.getParentId());
			if (dbMenu.getId() == parent.getParentId()) {
				return dbMenu;
			}
		}
		Menu parent = retrieve(newMenu.getParentId());
		newMenu.setLvl(parent.getLvl()+1);
		menuRepository.save(dbMenu.alterOf(newMenu));
		return dbMenu;
	}

	@Override
	public Long delete(Long id) {
		assertNotNull(id);
		Menu oldMenu = retrieve(id);
		menuRepository.remove(oldMenu);
		return id;
	}
	
	@Override
	public SearchResult<Menu> paginateAll(Map<String, Object> condition) {
		return menuRepository.paginateAll(condition);
	}

	@Override
	public List<Menu> all() {
		 List<Menu> all = menuRepository.findAll();
		all.sort(Comparator.comparing(Menu::getOrdinal));
		return all;
	}
	
	@Override
	public List<Menu> retrieveMenusByPerms(List<String> perms) {
		return menuRepository.getMenusByPerms(perms);
	}
}
