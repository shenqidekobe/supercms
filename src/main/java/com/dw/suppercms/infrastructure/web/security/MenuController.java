package com.dw.suppercms.infrastructure.web.security;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
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

import com.dw.suppercms.application.security.MenuService;
import com.dw.suppercms.domain.security.Menu;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Combotree;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.infrastructure.web.ui.TransformUtils;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * MenuController
 *
 * @author osmos
 * @date 2015年9月24日
 */
@RestController
@RequestMapping("/security")
public class MenuController extends BaseController {

	@Autowired
	private MenuService menuService;

	// validate title
	@RequestMapping(value = "/menus/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = menuService.validateTitle(title);
		} else {
			Menu menu = menuService.retrieve(id);
			if (!menu.getTitle().equals(title)) {
				valid = menuService.validateTitle(title);
			}
		}
		return valid;
	}

	// retrieve all menus represent as data table
	@RequestMapping(value = "/menus", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, int start, int length) {
		Map<String, Object> condition = new HashMap<>();
		condition.put("firstResult", start);
		condition.put("maxResults", length);
		SearchResult<Menu> data = menuService.paginateAll(condition);
		List<Menu> menus = data.getResult().stream().sorted(Comparator.comparing(Menu::getOrdinal))
				.collect(toList());
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), menus);
	}

	// retrieve all menus represent as combotree
	@RequestMapping(value = "/menus", method = { RequestMethod.GET }, params = { "tree" })
	public List<Combotree> combotree() {
		List<Menu> allMenus = menuService.all();
		return TransformUtils.transformToMenuCombotree(allMenus);
	}

	// retrieve a mneu by id
	@RequestMapping(value = "/menus/{id}", method = { RequestMethod.GET })
	public Menu id(@PathVariable Long id) {
		return menuService.retrieve(id);
	}

	// create menu
	@RequestMapping(value = "/menus", method = { RequestMethod.POST })
	@SystemLog(operation="创建新菜单",operType=OPER_TYPE.create)
	@Description("创建菜单")
	@RequiresPermissions({ "app.security.menu.create" })
	public void create(@RequestBody @Valid Menu menu) {
		menuService.create(menu);
		
		requestBodyAsLog(menu);
	}

	// save menu
	@RequestMapping(value = "/menus/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑菜单",operType=OPER_TYPE.save)
	@Description("修改菜单")
	@RequiresPermissions({ "app.security.menu.save" })
	public Menu save(@RequestBody @Valid Menu menu, BindingResult br) {
		menuService.update(menu.getId(), menu);
		
		requestBodyAsLog(menu);
		return menu;
	}

	// remove menu
	@RequestMapping(value = "/menus/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除菜单",operType=OPER_TYPE.remove)
	@Description("删除菜单")
	@RequiresPermissions({ "app.security.menu.remove" })
	public void remove(@PathVariable Long id) {
		menuService.delete(id);
	}
}
