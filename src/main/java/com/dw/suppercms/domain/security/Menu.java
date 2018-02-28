package com.dw.suppercms.domain.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Menu
 *
 * @author osmos
 * @date 2015年9月24日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, of = {})
public class Menu extends IdentifiedEntity {
	private static final long serialVersionUID = -1386033168975076343L;

	@NotNull
	@Size(max = 32)
	private String uisref;
	@NotNull
	@Size(max = 32)
	private String title;
	@Size(max = 32)
	private String icon;
	private Integer lvl;
	@NotNull
	private Float ordinal;
	
	private Long parentId;
	@ManyToOne
	@JoinColumn(name = "parentId", insertable = false, updatable = false)
	private Menu parent;
	@JsonIgnore
	@OneToMany(mappedBy = "parent")
	@OrderBy("ordinal ASC")
	private Set<Menu> children = new HashSet<Menu>();
	
	/**
	 * build a menu
	 * 
	 * @param menu holds the new bulding menu state
	 * @return the built menu
	 */
	public static Menu newOf(Menu menu) {
		Menu newMenu = Menu.newOf();
		newMenu.setUisref(menu.getUisref());
		newMenu.setTitle(menu.getTitle());
		newMenu.setIcon(menu.getIcon());
		newMenu.setLvl(menu.getLvl());
		newMenu.setOrdinal(menu.getOrdinal());
		newMenu.setParentId(menu.getParentId());
		return newMenu;
	}

	/**
	 * alter menu state
	 * 
	 * @param menu holds the altering site state
	 * @return the altered menu
	 */
	public Menu alterOf(Menu menu) {
		setTitle(menu.getTitle());
		setUisref(menu.getUisref());
		setTitle(menu.getTitle());
		setIcon(menu.getIcon());
		setLvl(menu.getLvl());
		setOrdinal(menu.getOrdinal());
		setParentId(menu.getParentId());
		return this;
	}
	
	public void setId(Long id) {
		super.setId(id);
	}
	
}
