package com.dw.suppercms.domain.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.dw.suppercms.domain.templates.TemplateSnippet;
import com.fasterxml.jackson.annotation.JsonIgnore;

import flexjson.JSON;

/**
 * 系统分类信息
 * */
@Entity
public class SysSortInfo extends IdentifiedEntity  {

	private static final long serialVersionUID = 1282567010871492991L;

	public enum SORT_TYPE {
		template_index,template_list,template_content,template_custom,template_snippet, custom_page,datasource,data_tag
	}
	private Long parentId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="parentId", insertable = false, updatable = false)
	private SysSortInfo parent; // 上级分类

	@NotNull
	@Size(max = 32)
	@Column(length = 32)
	private String sortName;// 分类名称

	@NotNull
	@Enumerated(EnumType.STRING)
	private SORT_TYPE sortType;// 类型
	
	@JsonIgnore
	@OneToMany(mappedBy="parent",fetch=FetchType.EAGER,cascade=CascadeType.MERGE)
	private Set<SysSortInfo> children=new HashSet<SysSortInfo>();//子分类
	
    @JSON(include=false)
	@JsonIgnore
	@OneToMany(mappedBy = "sort")
	@Where(clause="TEMPLATE_TYPE='index'")
	private Set<TemplateInfo> indexs; // 首页模版分类

    @JSON(include=false)
	@JsonIgnore
	@OneToMany(mappedBy = "sort")
	@Where(clause="TEMPLATE_TYPE='list'")
	private Set<TemplateInfo> lists; // 列表页模版分类

    @JSON(include=false)
	@JsonIgnore
	@OneToMany(mappedBy = "sort")
	@Where(clause="TEMPLATE_TYPE='content'")
	private Set<TemplateInfo> contents; // 最终页模版分类
	
    @JSON(include=false)
	@JsonIgnore
	@OneToMany(mappedBy = "sort")
	@Where(clause="TEMPLATE_TYPE='custom'")
	private Set<TemplateInfo> customs; // 最终页模版分类

	@JsonIgnore
	@OneToMany(mappedBy = "sort")
	private Set<TemplateSnippet> snippets;// 模版片段分类
	
	/*@OneToMany(mappedBy = "sort")
	private Set<CustomPage> custompages; // 自定义页分类

	@OneToMany(mappedBy = "sort")
	private Set<DataService> services; // 数据源分类

	@OneToMany(mappedBy = "sort")
	private Set<DataLabel> labels; // 数据标签分类

	@OneToMany(mappedBy = "sort")
	private Set<MemberInfo> members; // 投稿会员分类
    */
	
	
	

	public SysSortInfo getParent() {
		return parent;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setParent(SysSortInfo parent) {
		this.parent = parent;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public SORT_TYPE getSortType() {
		return sortType;
	}

	public void setSortType(SORT_TYPE sortType) {
		this.sortType = sortType;
	}

	public Set<SysSortInfo> getChildren() {
		return children;
	}

	public void setChildren(Set<SysSortInfo> children) {
		this.children = children;
	}

	public Set<TemplateInfo> getIndexs() {
		return indexs;
	}

	public void setIndexs(Set<TemplateInfo> indexs) {
		this.indexs = indexs;
	}

	public Set<TemplateInfo> getLists() {
		return lists;
	}

	public void setLists(Set<TemplateInfo> lists) {
		this.lists = lists;
	}

	public Set<TemplateInfo> getContents() {
		return contents;
	}

	public void setContents(Set<TemplateInfo> contents) {
		this.contents = contents;
	}

	public Set<TemplateInfo> getCustoms() {
		return customs;
	}

	public void setCustoms(Set<TemplateInfo> customs) {
		this.customs = customs;
	}

	public Set<TemplateSnippet> getSnippets() {
		return snippets;
	}

	public void setSnippets(Set<TemplateSnippet> snippets) {
		this.snippets = snippets;
	}
	
}
