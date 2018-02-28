package com.dw.suppercms.infrastructure.web.modules;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.modules.ColumnService;
import com.dw.suppercms.application.modules.SiteService;
import com.dw.suppercms.domain.data.ColumnData;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.ColumnCombotree;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.infrastructure.web.ui.TransformUtils;
import com.googlecode.genericdao.search.SearchResult;

import flexjson.JSONSerializer;

/**
 * 
 * ColumnController
 *
 * @author osmos
 * @date 2015年7月2日
 */
@RestController
@RequestMapping("/modules")
public class ColumnController extends BaseController {

	@Autowired
	private ColumnService columnService;
	@Autowired
	private SiteService siteService;

	// validate title
	@RequestMapping(value = "/columns/validateTitle", method = { RequestMethod.GET })
	public boolean validateTitle(Long id, String title) {
		boolean valid = true;
		if (id == null) {
			valid = columnService.validateTitle(title);
		} else {
			Column column = columnService.retrieve(id);
			if (!column.getTitle().equals(title)) {
				valid = columnService.validateTitle(title);
			}
		}
		return valid;
	}

	// validate direcotry
	@RequestMapping(value = "/columns/validateDirName", method = { RequestMethod.GET })
	public boolean validateDirName(Long id, String dirName) {
		boolean valid = true;
		dirName = StringUtils.prependIfMissing(dirName, "/", "/");
		if (id == null) {
			valid = columnService.validateDirName(dirName);
		} else {
			Column column = columnService.retrieve(id);
			if (!column.getDirName().equals(dirName)) {
				valid = columnService.validateDirName(dirName);
			}
		}
		return valid;
	}

	// retrieve all columns as list
	@RequestMapping(value = "/columns", method = { RequestMethod.GET })
	public List<Column> all() {
		return columnService.all();
	}

	// retrieve all site columns as list
	@RequestMapping(value = "/columns", method = { RequestMethod.GET }, params = { "siteColumn" })
	public Map<String, List<Column>> siteColumn() {
		Map<String, List<Column>> map = new HashMap<String, List<Column>>();
		List<Site> sites = this.siteService.all();
		for (Site site : sites) {
			SearchResult<Column> sr = this.columnService.retrieveBySite(site.getId(), 0, Integer.MAX_VALUE);
			map.put(site.getTitle(), sr.getResult());
		}
		return map;
	}

	// retrieve all columns represent as data table
	@RequestMapping(value = "/columns", method = { RequestMethod.GET }, params = { "datatable" })
	public String datatable(int draw, Long siteId, int start, int length) {
		SearchResult<Column> data = columnService.retrieveBySite(siteId, start, length);
		List<Column> columns = data.getResult().stream()
				.sorted(Comparator.comparing(Column::getDirDiskpath))
				.collect(toList());
		Datatable datatable = new Datatable(draw, data.getTotalCount(), data.getTotalCount(), columns);
		String json = new JSONSerializer()
				.include("data.site.title", "data.site.dirName",
						"data.homeTemplate.templateName",
						"data.contentTemplate.templateName", "data.datasource.title")
				.exclude("*.class", "*.site.*", "*.homeTemplate.*",
						"*.contentTemplate.*", "*.datasource.*")
				.serialize(datatable);
		return json;
	}

	// retrieve all columns represent as combotree
	@RequestMapping(value = "/columns", method = { RequestMethod.GET }, params = { "tree" })
	public List<ColumnCombotree> combotree() {
		List<Site> allSites = siteService.all();
		List<Column> allColumns = columnService.all().stream()
				.sorted(Comparator.comparing(Column::getDirDiskpath))
				.collect(toList());
		return TransformUtils.transformToCombotree(allSites, allColumns, new HashSet<Column>());
	}

	// retrieve columns represent as combotree of user
	@RequestMapping(value = "/columns", method = { RequestMethod.GET }, params = { "owntree" })
	public List<ColumnCombotree> ownTree() {
		List<Site> allSites = siteService.all();

		List<Column> ownColumns = columnService.own().stream()
				.sorted(Comparator.comparing(Column::getDirDiskpath))
				.collect(toList());

		Set<Column> notOwnColumns = new HashSet<Column>();
		Function<Column, Void> fn = new Function<Column, Void>() {
			@Override
			public Void apply(Column c) {
				if (c.getParentId() != null) {
					if (!ownColumns.contains(c.getParent())) {
						notOwnColumns.add(c.getParent());
					}
					this.apply(c.getParent());
				}
				return null;
			}
		};
		ownColumns.stream().forEach(new Consumer<Column>() {
			@Override
			public void accept(Column c) {
				fn.apply(c);
			}
		});
		return TransformUtils.transformToCombotree(allSites, ownColumns, notOwnColumns);
	}

	// retrieve columns by dataIds
	@RequestMapping(value = "/columns", method = { RequestMethod.GET }, params = { "dataIds" })
	public List<ColumnData> ids(String dataId) {
		if (StringUtils.isEmpty(dataId)) {
			return null;
		} else {
			String[] splits = dataId.split(",");
			Long[] dataIds = new Long[splits.length];
			for (int i = 0; i < splits.length; i++) {
				dataIds[i] = Long.parseLong(splits[i]);
			}
			return columnService.retrieveByDataIds(dataIds);
		}
	}

	// retrieve a column by id
	@RequestMapping(value = "/columns/{id}", method = { RequestMethod.GET })
	public Column id(@PathVariable Long id) {
		return columnService.retrieve(id);
	}

	// create column
	@RequestMapping(value = "/columns", method = { RequestMethod.POST })
	@SystemLog(operation = "创建栏目", operType = OPER_TYPE.create)
	@Description("创建栏目")
	@RequiresPermissions({ "app.modules.column.create" })
	public void create(@RequestBody @Valid Column column) {
		columnService.create(column);
		requestBodyAsLog(column.getTitle());
	}

	// save column
	@RequestMapping(value = "/columns/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑栏目", operType = OPER_TYPE.save)
	@Description("修改栏目")
	@RequiresPermissions({ "app.modules.column.save" })
	public Column save(@RequestBody @Valid Column column, BindingResult br) {
		columnService.update(column.getId(), column);
		requestBodyAsLog(column.getTitle());
		return column;
	}

	// remove column
	@RequestMapping(value = "/columns/{id}", method = { RequestMethod.DELETE })
	@SystemLog(operation = "删除栏目", operType = OPER_TYPE.remove)
	@Description("删除栏目")
	@RequiresPermissions({ "app.modules.column.remove" })
	public void remove(@PathVariable Long id) {
		columnService.delete(id);
	}

}
