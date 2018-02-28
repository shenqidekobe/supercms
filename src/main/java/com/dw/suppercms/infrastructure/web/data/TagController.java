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

import com.dw.suppercms.application.data.TagService;
import com.dw.suppercms.domain.data.Tag;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 
 * TagController
 *
 * @author osmos
 * @date 2015年9月8日
 */
@RestController
@RequestMapping("/datas")
public class TagController extends BaseController{

	@Autowired
	private TagService tagService;

	// validate title
	@RequestMapping(value = "/tags/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = tagService.validateTitle(title);
		} else {
			Tag tag = tagService.retrieve(id);
			if (!tag.getTitle().equals(title)) {
				valid = tagService.validateTitle(title);
			}
		}
		return valid;
	}

	// retrieve all tags as list
	@RequestMapping(value = "/tags", method = { RequestMethod.GET })
	public List<Tag> all() {
		return tagService.all();
	}

	// retrieve all tags as datatable
	@RequestMapping(value = "/tags", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long sortId, int start, int length) {
		Map<String, Object> conditon = new HashMap<>();
		conditon.put("firstResult", start);
		conditon.put("maxResults", length);
		conditon.put("sorts", Lists.newArrayList(new Sort("createTime", true)));
		SearchResult<Tag> data = tagService.paginateAll(conditon);
		return new Datatable(draw, data.getTotalCount(), data.getTotalCount(), data.getResult());
	}

	// retreive a tag by id
	@RequestMapping(value = "/tags/{id}", method = { RequestMethod.GET })
	public Tag id(@PathVariable Long id) {
		return tagService.retrieve(id);
	}

	// retrieve tags by dataId
	@RequestMapping(value = "/tags", method = { RequestMethod.GET }, params = { "owns" })
	public List<Tag> dataId(Long id, Long datasourceId) {
		List<Tag> list= tagService.tagsOnData(datasourceId, id);
		return list;
	}

	// create tag
	@RequestMapping(value = "/tags", method = { RequestMethod.POST })
	@SystemLog(operation="创建数据标签",operType=OPER_TYPE.create)
	@Description("创建标签")
	@RequiresPermissions({ "app.datas.tag.create" })
	public Tag create(@RequestBody @Valid Tag tag) {
		Tag newTag = tagService.create(tag);
		requestBodyAsLog(newTag);
		return newTag;
	}

	// save tag
	@RequestMapping(value = "/tags/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation="编辑数据标签",operType=OPER_TYPE.save)
	@Description("修改标签")
	@RequiresPermissions({ "app.datas.tag.save" })
	public Tag save(@RequestBody @Valid Tag tag, BindingResult br) {
		tagService.update(tag.getId(), tag);
		requestBodyAsLog(tag);
		return tag;
	}

	// remove tag
	@RequestMapping(value = "/tags/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation="删除数据标签",operType=OPER_TYPE.remove)
	@Description("删除标签")
	@RequiresPermissions({ "app.datas.tag.remove" })
	public void remove(@PathVariable Long id) {
		tagService.delete(id);
	}
}
