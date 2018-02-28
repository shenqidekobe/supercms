package com.dw.suppercms.infrastructure.web.ui;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Module.ModuleType;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.domain.security.Menu;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.google.common.collect.Lists;

public class TransformUtils {

	public static List<ColumnCombotree> transformToCombotree(List<Site> sites, List<Column> columns, Set<Column> notOwnColumns) {

		Map<Long, ColumnCombotree> sitesCombotree = sites.stream()
				.collect(
						toMap(Site::getId, s -> new ColumnCombotree(s.getId(), s.getTitle(), null, s.getId(), null,
								ModuleType.SITE, true)));

		Map<Long, ColumnCombotree> columnsCombotree = columns.stream().collect(
				toMap(Column::getId,
						c -> new ColumnCombotree(c.getId(), c.getTitle(), c.getParentId(), c.getSiteId(), c
								.getDatasourceId(), ModuleType.COLUMN, true)));
		
		notOwnColumns.stream().forEach(c -> {
			columns.add(c);
			columnsCombotree.put(c.getId(), new ColumnCombotree(c.getId(), c.getTitle(), c.getParentId(), c.getSiteId(), c
					.getDatasourceId(), ModuleType.COLUMN, false));
		});

		columns.stream()
				.filter(c -> c.getParentId() != null)
				.forEach(c -> {
					Long parentId = c.getParentId();
					Long id = c.getId();
					columnsCombotree.get(parentId).getChildrens().add(columnsCombotree.get(id));
				});
		
		columns.stream()
				.filter(c -> c.getParent() == null)
				.forEach(c -> {
					sitesCombotree.get(c.getSiteId()).getChildrens().add(columnsCombotree.get(c.getId()));
				});
		
		return Lists.newArrayList(sitesCombotree.values());
	}

	public static List<Combotree> transformToSortCombotree(List<SysSortInfo> sorts) {
		Map<Long, Combotree> combotreeMap = sorts.stream().collect(toMap(SysSortInfo::getId,
				s -> new Combotree(s.getId(), s.getSortName(), s.getParent() == null ? null : s.getParent().getId())));

		sorts.stream().filter(s -> s.getParent() != null).forEach(
				s -> {
					combotreeMap.get(s.getParent().getId()).getChildrens().add(combotreeMap.get(s.getId()));
				});

		Map<Long, Combotree> topCombotreeMap = combotreeMap.values().stream()
				.filter(c -> c.getParentId() == null).collect(
						toMap(Combotree::getId, c -> combotreeMap.get(c.getId())));

		return Lists.newArrayList(topCombotreeMap.values());
	}
	
	public static List<Combotree> transformToMenuCombotree(List<Menu> menus) {
		Map<Long, Combotree> combotreeMap = menus.stream().collect(toMap(Menu::getId,
				s -> new Combotree(s.getId(), s.getTitle(), s.getParent() == null ? null : s.getParent().getId())));

		menus.stream().filter(s -> s.getParent() != null).forEach(
				s -> {
					combotreeMap.get(s.getParent().getId()).getChildrens().add(combotreeMap.get(s.getId()));
				});

		Map<Long, Combotree> topCombotreeMap = combotreeMap.values().stream()
				.filter(c -> c.getParentId() == null).collect(
						toMap(Combotree::getId, c -> combotreeMap.get(c.getId())));
		List<Combotree> list = Lists.newArrayList(topCombotreeMap.values());
		Combotree combotree = new Combotree(null, "无上级菜单", null);
		combotree.setChildrens(list);
		return Lists.newArrayList(combotree);
	}
}
