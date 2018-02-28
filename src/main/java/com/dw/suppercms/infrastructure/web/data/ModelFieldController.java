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

import com.dw.suppercms.application.data.ModelFieldService;
import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelField.FormType;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * ModelFieldController
 *
 * @author osmos
 * @date 2015年8月7日
 */
@RestController
@RequestMapping("/datas")
public class ModelFieldController extends BaseController {

	@Autowired
	private ModelFieldService fieldService;
	@Autowired
	private SysSortService sortService;

	// validate title
	@RequestMapping(value = "/fields/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = fieldService.validateTitle(title);
		} else {
			ModelField field = fieldService.retrieve(id);
			if (!field.getTitle().equals(title)) {
				valid = fieldService.validateTitle(title);
			}
		}
		return valid;
	}

	// validate field code
	@RequestMapping(value = "/fields/validateCode", method = { RequestMethod.GET })
	public boolean validateCode(Long id, String code) {
		boolean valid = true;
		if (id == null) {
			valid = fieldService.validateFieldCode(code);
		} else {
			ModelField field = fieldService.retrieve(id);
			if (!field.getFieldCode().equals(code)) {
				valid = fieldService.validateFieldCode(code);
			}
		}
		return valid;
	}

	// retrieve all fields of model as datatable
	@RequestMapping(value = "/fields", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long modelId, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("modelId", modelId);
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		conditon.put("sorts", Lists.newArrayList(new Sort("ordinal", false)));
		SearchResult<ModelField> data = fieldService.paginateAll(conditon);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}
	
	// 查看模型字段
	@RequestMapping(value = "/fields", method = { RequestMethod.GET }, params = { "fields" })
	public List<ModelField> fields(Long modelId) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("modelId", modelId);
		conditon.put("firstResult", 0);
		conditon.put("maxResults", Integer.MAX_VALUE);
		conditon.put("sorts", Lists.newArrayList(new Sort("ordinal", false)));
		SearchResult<ModelField> data = fieldService.paginateAll(conditon);
		List<ModelField> results=data.getResult();
		//手动添加默认的字段
		ModelField field1=ModelField.newOf();
		field1.setTitle("编辑");
		field1.setFieldCode("editor");
		field1.setFormType(FormType.TEXT.toString());
		results.add(field1);
		ModelField field2=ModelField.newOf();
		field2.setTitle("创建时间");
		field2.setFieldCode("create_time");
		field2.setFormType(FormType.DATE.toString());
		results.add(field2);
		ModelField field3=ModelField.newOf();
		field3.setTitle("发布时间");
		field3.setFieldCode("publish_time");
		field3.setFormType(FormType.DATE.toString());
		results.add(field3);
		return results;
	}

	// retreive a field by id
	@RequestMapping(value = "/fields/{id}", method = { RequestMethod.GET })
	public ModelField id(@PathVariable Long id) {
		return fieldService.retrieve(id);
	}

	// create field
	@RequestMapping(value = "/fields", method = { RequestMethod.POST })
	@SystemLog(operation="创建模型字段属性",operType=OPER_TYPE.create)
	@Description("创建字段")
	@RequiresPermissions({ "app.datas.model.createField" })
	public ModelField create(@RequestBody @Valid ModelField field) {
		ModelField newModelField = fieldService.create(field);
		requestBodyAsLog(newModelField);
		return newModelField;
	}

	// save field
	@RequestMapping(value = "/fields/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑模型字段属性",operType=OPER_TYPE.save)
	@Description("修改字段")
	@RequiresPermissions({ "app.datas.model.saveField" })
	public ModelField save(@RequestBody @Valid ModelField field, BindingResult br) {
		fieldService.update(field.getId(), field);
		requestBodyAsLog(field);
		return field;
	}

	// remove field
	@RequestMapping(value = "/fields/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除模型字段属性",operType=OPER_TYPE.remove)
	@Description("删除字段")
	@RequiresPermissions({ "app.datas.model.removeField" })
	public void remove(@PathVariable Long id) {
		fieldService.delete(id);
	}
}
