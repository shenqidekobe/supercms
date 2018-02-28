package com.dw.suppercms.domain.modules;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import com.dw.framework.core.SpringHelper;
import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.templates.TemplateInfo;

/**
 * Site
 * <p>
 * represent a static web site. a site has a home page template
 * </p>
 * 
 * @author osmos
 * @date 2015年6月12日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Site extends IdentifiedEntity implements Module {
	private static final long serialVersionUID = -9132572746280234276L;

	// ----------------------------------------Simple Fiels

	// title of site
	@NotNull
	@Size(max = 32)
	private String title;

	// description of site
	@NotNull
	@Size(max = 64)
	private String description;

	// domain of test environment
	@NotNull
	@Size(max = 32)
	private String testDomain;

	// domain of product environment
	@NotNull
	@Size(max = 32)
	private String productDomain;

	// directory name
	@NotNull
	@Size(max = 32)
	private String dirName;

	// --------------------------------------------Associate Fiels

	// associate home template id
	@NotNull
	private Long homeTemplateId;

	// associate home template object
	@ManyToOne
	@JoinColumn(name = "homeTemplateId", insertable = false, updatable = false)
	private TemplateInfo homeTemplate;

	// ---------------------------------------------Supper Interface Implements

	@Override
	public String getModuleType() {
		return ModuleType.SITE.name();
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
		String url = getTestDomain() + getFileWebpath();
		return StringUtils.prependIfMissing(url, "http://", "http://");
	}

	@Override
	public String getProductUrl() {
		String url = getProductDomain() + getFileWebpath();
		return StringUtils.prependIfMissing(url, "http://", "http://");
	}

	@Override
	public String getDirWebpath() {
		return "/";
	}

	@Override
	public String getDirDiskpath() {
		String dirName = StringUtils.prependIfMissing(getDirName(), "/", "/");
		return SpringHelper.servletContext.getRealPath("/") + MAKE_DIR
				+ dirName;
	}

	@Override
	public String getFileWebpath() {
		return getDirWebpath() + "/" + getFileName() + "." + getExtensionName();
	}

	@Override
	public String getFileDiskpath() {
		return getDirDiskpath() + getFileWebpath();
	}

	@Override
	public String getFileWebpath(int pageth) {
		String pagethPost = (pageth == 1 ? "" : ("-" + pageth));
		return getDirWebpath() + "/" + getFileName() + pagethPost + "."
				+ getExtensionName();
	}

	@Override
	public String getFileDiskpath(int pageth) {
		return getDirDiskpath() + getFileWebpath(pageth);
	}
	
	@Transient
	public String getPubDiskpath(){
		String dirName = StringUtils.prependIfMissing(getDirName(), "/", "/");
		return SpringHelper.servletContext.getRealPath("/") +PUB_DIR + dirName;
	}
	
	@Transient
	public String getPubFileDiskpath(){
		return getPubDiskpath()+getFileWebpath();
	}

	// -----------------------------------------------------Own Implements

	/**
	 * build a new site
	 * 
	 * @param site
	 *            holds the new building site state
	 * @return the built site
	 */
	public static Site newOf(Site site) {
		Site newSite = Site.newOf();
		newSite.setTitle(site.getTitle());
		newSite.setDescription(site.getDescription());
		newSite.setDirName(StringUtils.prependIfMissing(site.getDirName(), "/",
				"/"));
		newSite.setTestDomain(site.getTestDomain());
		newSite.setProductDomain(site.getProductDomain());
		newSite.setHomeTemplateId(site.getHomeTemplateId());
		return newSite;
	}

	/**
	 * alter site state
	 * 
	 * @param site
	 *            holds the altering site state
	 * @return the altered site
	 */
	public Site alterOf(Site site) {
		setTitle(site.getTitle());
		setDescription(site.getDescription());
		setDirName(StringUtils.prependIfMissing(site.getDirName(), "/", "/"));
		setProductDomain(site.getProductDomain());
		setTestDomain(site.getTestDomain());
		setHomeTemplateId(site.getHomeTemplateId());
		return this;
	}
}
