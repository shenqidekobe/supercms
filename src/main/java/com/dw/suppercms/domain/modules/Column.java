package com.dw.suppercms.domain.modules;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.templates.TemplateInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Column
 * <p>
 * represent a column which may has a parent column, or some child columns.a
 * column must belong of a site, a column has one home page template and a
 * content page template.
 * </p>
 *
 * @author osmos
 * @date 2015年7月2日
 */
@Entity
@Table(name = "channel")
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Column
		extends IdentifiedEntity
		implements Module {
	private static final long serialVersionUID = 7293467243138341772L;

	// --------------------------------------------Simple Fiels

	// title of column
	@NotNull
	@Size(max = 32)
	private String title;

	// description of column
	@NotNull
	@Size(max = 64)
	private String description;

	// directory name
	@NotNull
	@Size(max = 16)
	private String dirName;

	// --------------------------------------------associate Fiels

	@NotNull
	// associate datasource id
	private Long datasourceId;

	// associate home page template id
	@NotNull
	private Long homeTemplateId;

	// associate content page template id
	@NotNull
	private Long contentTemplateId;

	// associate parent column id
	private Long parentId;

	// associate belong of site id
	@NotNull
	private Long siteId;
	
	//生成列表页的状态，默认false
	private boolean makeListState=false;

	// associate datasource object
	@ManyToOne
	@JoinColumn(name = "datasourceId", insertable = false, updatable = false)
	private Datasource datasource;

	// associate home page template object
	@ManyToOne
	@JoinColumn(name = "homeTemplateId", insertable = false, updatable = false)
	private TemplateInfo homeTemplate;

	// associate content page template object
	@ManyToOne
	@JoinColumn(name = "contentTemplateId", insertable = false, updatable = false)
	private TemplateInfo contentTemplate;

	// associate parent column object
	@ManyToOne
	@JoinColumn(name = "parentId", insertable = false, updatable = false)
	private Column parent;

	// associate child columns objects
	@JsonIgnore
	@OneToMany(mappedBy = "parent")
	private Set<Column> children = new HashSet<Column>();

	// associate belong of site object
	@ManyToOne
	@JoinColumn(name = "siteId", insertable = false, updatable = false)
	private Site site;

	// --------------------------------------------Supper Interface Implements

	@Override
	public String getModuleType() {
		return ModuleType.COLUMN.name();
	}

	@Override
	public String getFileName() {
		return "index";
	}

	@Override
	public String getExtensionName() {
		return ExtensionName.shtml.name();
	}

	@Override
	public String getTestUrl() {
		return getSite().getTestDomain() + getFileWebpath();
	}

	@Override
	public String getProductUrl() {
		return getSite().getProductDomain() + getFileWebpath();
	}

	@Override
	public String getDirWebpath() {
		String result = getDirName();
		if (getParent() != null) {
			result = getParent().getDirWebpath() + result;
		}
		return result;
	}

	@Override
	public String getDirDiskpath() {
		return getSite().getDirDiskpath() + getDirWebpath();
	}

	@Override
	public String getFileWebpath() {
		return getDirWebpath() + "/" + getFileName() + "." + getExtensionName();
	}

	@Override
	public String getFileDiskpath() {
		return getDirDiskpath() + "/" + getFileName() + "." + getExtensionName();
	}

	@Override
	public String getFileWebpath(int pageth) {
		String pagethPost = (pageth == 1 ? "" : ("-" + pageth));
		return getDirWebpath() + "/" + getFileName() + pagethPost + "." + getExtensionName();
	}
	
	@Transient
	public String getPubDiskpath(){
		return getSite().getPubDiskpath() + getDirWebpath();
	}

	/**
	 * 列表页文件生成的全地址
	 * */
	@Override
	public String getFileDiskpath(int pageth) {
		return getSite().getDirDiskpath() + getFileWebpath(pageth);
	}
	
	
	/**
	 * 历史列表文件的相对路径,按月
	 * */
	@Transient
	public String getHistoryListFileWebpath(Date date,int pageth){
		DateFormat sf = new SimpleDateFormat("yyyy-MM");
		String datePath = sf.format(date);
		String pagethPost = (pageth == 1 ? "" : ("-" + pageth));
		return getDirWebpath() + "/" +datePath + "/"+getFileName() + pagethPost + "." + getExtensionName();
	}
	
	/**
	 * 历史列表文件的全地址(生成地址)
	 * */
	@Transient
	public String getHistoryListFileDiskpath(Date date,int pageth){
		 return getSite().getDirDiskpath()+getHistoryListFileWebpath(date,pageth);
	}

	@Transient
	public Integer getSeries() {
		Integer series = 1;
		if (getParent() != null) {
			series += getParent().getSeries();
		}
		return series;
	}

	/**
	 * 最终页文件生成的全地址
	 * */
	@Transient
	public String getContentFilePath(String id, Timestamp createTimePath) {
		return getSite().getDirDiskpath()+getContentWebPath(id, createTimePath);
	}

	/**
	 * 最终页文件生成的相对地址
	 * */
	@Transient
	public String getContentWebPath(String id, Timestamp createTimePath) {
		String pattren = "yyyy-MM-dd";
		String date = "";
		if (null == createTimePath) {
			date = DateFormatUtils.format(new Date(), pattren);
		} else {
			DateFormat sf = new SimpleDateFormat(pattren);
			date = sf.format(createTimePath);
		}
		String fullPath = getDirWebpath() + "/" + date + "/a" + id + "." + getExtensionName();
		return fullPath;
	}

	public String getDetail() {
		String result = getTitle();
		if (getParent() != null) {
			result = getParent().getDetail() + "&nbsp;<i class='fa fa-long-arrow-right'></i>&nbsp;" + result;
		}
		return result;
	}

	// --------------------------------------------Own Implements

	/**
	 * build a column
	 * 
	 * @param column holds the new bulding column state
	 * @return the built column
	 */
	public static Column newOf(Column column) {
		Column newColumn = Column.newOf();
		newColumn.setTitle(column.getTitle());
		newColumn.setDescription(column.getDescription());
		newColumn.setDirName(StringUtils.prependIfMissing(column.getDirName(), "/", "/"));
		newColumn.setDatasourceId(column.getDatasourceId());
		newColumn.setHomeTemplateId(column.getHomeTemplateId());
		newColumn.setContentTemplateId(column.getContentTemplateId());
		newColumn.setParentId(column.getParentId());
		newColumn.setSiteId(column.getSiteId());
		return newColumn;
	}

	/**
	 * alter column state
	 * 
	 * @param column holds the altering site state
	 * @return the altered column
	 */
	public Column alterOf(Column column) {
		setTitle(column.getTitle());
		setDescription(column.getDescription());
		setDirName(StringUtils.prependIfMissing(column.getDirName(), "/", "/"));
		setDatasourceId(column.getDatasourceId());
		setHomeTemplateId(column.getHomeTemplateId());
		setContentTemplateId(column.getContentTemplateId());
		setParentId(column.getParentId());
		setSiteId(column.getSiteId());
		return this;
	}
}
