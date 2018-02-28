package com.dw.suppercms.infrastructure.web.security;

import static java.util.stream.Collectors.toList;

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

import com.dw.suppercms.application.data.DatasourceService;
import com.dw.suppercms.application.security.UserService;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.googlecode.genericdao.search.SearchResult;

/**
 * UserController
 *
 * @author osmos
 * @date 2015年9月17日
 */
@RestController
@RequestMapping("/security")
@SuppressWarnings("unchecked")
public class UserController extends BaseController {
	@Autowired
	private UserService userService;
	@Autowired
	private DatasourceService datasourceService;

	// validate username
	@RequestMapping(value = "/users/validateUsername", method = { RequestMethod.GET })
	public boolean validateName(Long id, String username) {
		boolean valid = true;
		if (id == null) {
			valid = userService.validateUsername(username);
		} else {
			User user = userService.retrieve(id);
			if (!user.getName().equals(username)) {
				valid = userService.validateUsername(username);
			}
		}
		return valid;
	}

	/**
	 * 获取所有信息
	 * */
	@RequestMapping(value = "/users", method = { RequestMethod.GET })
	public List<User> all() {
		return userService.all();
	}

	// retrieve all users as datatable
	@RequestMapping(value = "/users", method = { RequestMethod.GET }, params = { "datatable" })
	public String datatable(int draw, Long sortId, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		SearchResult<User> data = userService.paginateAll(conditon);
		Datatable datatable = new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
		String json = new flexjson.JSONSerializer()
				.include("data.role.id", "data.role.name")
				.exclude("*.class", "data.role.*")
				.serialize(datatable);
		return json;
	}

	// retreive a user by id
	@RequestMapping(value = "/users/{id}", method = { RequestMethod.GET })
	public User id(@PathVariable Long id) {
		return userService.retrieve(id);
	}

	// create user
	@RequestMapping(value = "/users", method = { RequestMethod.POST })
	@SystemLog(operation = "创建系统用户", operType = OPER_TYPE.create)
	@Description("创建用户")
	@RequiresPermissions({ "app.security.user.create" })
	public User create(@RequestBody @Valid User user) {
		User newUser = userService.create(user);
		requestBodyAsLog(newUser);
		return newUser;
	}

	// save user
	@RequestMapping(value = "/users/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑系统用户", operType = OPER_TYPE.save)
	@Description("修改用户")
	@RequiresPermissions({ "app.security.user.save" })
	public User save(@RequestBody @Valid User user, BindingResult br) {
		userService.update(user.getId(), user);
		requestBodyAsLog(user);
		return user;
	}

	// remove user
	@RequestMapping(value = "/users/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation = "删除系统用户", operType = OPER_TYPE.remove)
	@Description("删除用户")
	@RequiresPermissions({ "app.security.user.remove" })
	public void remove(@PathVariable Long id) {
		userService.delete(id);
	}

	// load datasources of user
	@RequestMapping(value = "/users", method = { RequestMethod.GET }, params = { "datasources" })
	public List<Long> getDatasources(Long userId) {
		return datasourceService.retrieveDatasourceIds(userId);
	}

	// assign datasources to user
	@RequestMapping(value = "/users", method = { RequestMethod.POST }, params = { "assignDatasources" })
	public void assignDatasources(@RequestBody Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("userId").toString());
		List<Integer> datasourceIds = (List<Integer>) params.get("datasourceIds");
		datasourceService.assignDatasources(userId, datasourceIds.stream().map(d -> Long.parseLong(d.toString()))
				.collect(toList()));
	}
	@Description("站点目录")
	@RequiresPermissions({ "app.filemanager.sitesDir" })
	public void sitesDir(){
		
	}
}
