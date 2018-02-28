package com.dw.suppercms.domain.modules;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import com.dw.framework.core.SpringHelper;
import com.dw.framework.core.annotation.StringEnumeration;
import com.dw.framework.core.domain.IdentifiedEntity;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.templates.TemplateInfo;

/**
 * Custom
 * <p>
 * a custom must belong of a sort
 * </p>
 * 
 * @author osmos
 * @date 2015年7月14日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Custom
		extends IdentifiedEntity
		implements Module {
	private static final long serialVersionUID = -5355748327975350960L;

	// --------------------------------------------Simple Fiels

	// title of custom page
	@NotNull
	@Size(max = 32)
	private String title;

	// description of custom
	@NotNull
	@Size(max = 64)
	private String description;

	// file path with no extension name from the make directory
	@NotNull
	@Size(max = 48)
	private String location;

	// file extension name
	@NotNull
	@Size(max = 8)
	@StringEnumeration(enumClass = ExtensionName.class)
	private String extensionName;

	// --------------------------------------------associate Fiels

	// associate belong of sort id
	@NotNull
	private Long sortId;

	// associate custome template id
	@NotNull
	private Long customTemplateId;
	
	@Lob
	private String templateCode;

	// associate belong of sort object
	@ManyToOne
	@JoinColumn(name = "sortId", insertable = false, updatable = false)
	private SysSortInfo sort;

	// associate custome template object
	@ManyToOne
	@JoinColumn(name = "customTemplateId", insertable = false, updatable = false)
	private TemplateInfo customTemplate;

	// --------------------------------------------Supper Interface Implements

	// from make dir
	@Override
	public String getDirName() {
		return StringUtils.substringBeforeLast(getLocation(), "/");
	}

	@Override
	public String getFileName() {
		return StringUtils.substringAfterLast(getLocation(), "/");
	}

	@Override
	public String getModuleType() {
		return ModuleType.CUSTOM.name();
	}

	@Override
	public String getTestUrl() {
		return "";
	}

	@Override
	public String getProductUrl() {
		return "";
	}

	@Override
	public String getDirWebpath() {
		return "/" + StringUtils.substringAfter(StringUtils.substringAfter(getDirName(), "/"), "/");
	}

	@Override
	public String getDirDiskpath() {
		return SpringHelper.servletContext.getRealPath("/") + MAKE_DIR + getDirName();
	}

	@Override
	public String getFileWebpath() {
		return getDirWebpath() + "/" + getFileName() + "." + getExtensionName() + "." + getExtensionName();
	}

	@Override
	public String getFileDiskpath() {
		return SpringHelper.servletContext.getRealPath("/") + MAKE_DIR + getLocation() + "." + getExtensionName();
	}
	
	@Transient
	public String getPubFileDiskpath() {
		return SpringHelper.servletContext.getRealPath("/") + PUB_DIR + getLocation() + "." + getExtensionName();
	}


	// --------------------------------------------Own Implements

	/**
	 * build a custom
	 * 
	 * @param custom holds the new bulding custom state
	 * @return the built custom
	 */
	public static Custom newOf(Custom custom) {
		Custom newCustom = Custom.newOf();
		newCustom.setTitle(custom.getTitle());
		newCustom.setDescription(custom.getDescription());
		newCustom.setLocation(StringUtils.prependIfMissing(custom.getLocation(), "/", "/"));
		newCustom.setExtensionName(custom.getExtensionName());
		newCustom.setCustomTemplateId(custom.getCustomTemplateId());
		newCustom.setSortId(custom.getSortId());
		return newCustom;
	}

	/**
	 * alter custom state
	 * 
	 * @param custom holds the altering site state
	 * @return the altered custom
	 */
	public Custom alterOf(Custom custom) {
		setTitle(custom.getTitle());
		setDescription(custom.getDescription());
		setLocation(StringUtils.prependIfMissing(custom.getLocation(), "/", "/"));
		setExtensionName(custom.getExtensionName());
		setCustomTemplateId(custom.getCustomTemplateId());
		setSortId(custom.getSortId());
		return this;
	}

	@Override
	public String getFileWebpath(int pageth) {
		return null;
	}

	@Override
	public String getFileDiskpath(int pageth) {
		return null;
	}
}