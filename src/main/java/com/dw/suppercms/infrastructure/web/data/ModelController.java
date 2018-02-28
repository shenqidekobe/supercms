package com.dw.suppercms.infrastructure.web.data;

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
import com.dw.suppercms.application.data.ModelService;
import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * ModelController
 *
 * @author osmos
 * @date 2015年7月31日
 */
@RestController
@RequestMapping("/datas")
public class ModelController extends BaseController {

	@Autowired
	private ModelService modelService;
	@Autowired
	private DatasourceService datasourceService;
	@Autowired
	private SysSortService sortService;

	// validate title
	@RequestMapping(value = "/models/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = modelService.validateTitle(title);
		} else {
			Model model = modelService.retrieve(id);
			if (!model.getTitle().equals(title)) {
				valid = modelService.validateTitle(title);
			}
		}
		return valid;
	}

	// validate table code
	@RequestMapping(value = "/models/validateCode", method = { RequestMethod.GET })
	public boolean validateCode(Long id, String code) {
		boolean valid = true;
		if (id == null) {
			valid = modelService.validateTableCode(code);
		} else {
			Model model = modelService.retrieve(id);
			if (!model.getTableCode().equals(code)) {
				valid = modelService.validateTableCode(code);
			}
		}
		return valid;
	}

	// retrieve all models as list
	@RequestMapping(value = "/models", method = { RequestMethod.GET })
	public List<Model> all() {
		return modelService.all();
	}

	// retrieve all models as datatable
	@RequestMapping(value = "/models", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		conditon.put("sorts", Lists.newArrayList(new Sort("createTime", true)));
		SearchResult<Model> data = modelService.paginateAll(conditon);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	// retreive a model by id
	@RequestMapping(value = "/models/{id}", method = { RequestMethod.GET })
	public Model id(@PathVariable Long id) {
		return modelService.retrieve(id);
	}

	// retreive a model by datasource id
	@RequestMapping(value = "/models", method = { RequestMethod.GET }, params = { "datasourceId" })
	public Model byDatasource(Long datasourceId) {
		return modelService.retrieve(datasourceService.retrieve(datasourceId).getModel().getId());
	}

	// create model
	@RequestMapping(value = "/models", method = { RequestMethod.POST })
	@SystemLog(operation="创建数据模型",operType=OPER_TYPE.create)
	@Description("创建模型")
	@RequiresPermissions({ "app.datas.model.create" })
	public Model create(@RequestBody @Valid Model model) {
		Model newModel = modelService.create(model);
		requestBodyAsLog(newModel.getTitle());
		return newModel;
	}

	// save model
	@RequestMapping(value = "/models/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑数据模型",operType=OPER_TYPE.save)
	@Description("修改模型")
	@RequiresPermissions({ "app.datas.model.save" })
	public Model save(@RequestBody @Valid Model model, BindingResult br) {
		modelService.update(model.getId(), model);
		requestBodyAsLog(model.getTitle());
		return model;
	}

	// remove model
	@RequestMapping(value = "/models/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除数据模型",operType=OPER_TYPE.remove)
	@Description("删除模型")
	@RequiresPermissions({ "app.datas.model.remove" })
	public void remove(@PathVariable Long id) {
		modelService.delete(id);
	}
}
