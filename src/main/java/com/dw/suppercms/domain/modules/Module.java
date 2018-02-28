package com.dw.suppercms.domain.modules;

/**
 * Module
 * <p>
 * all types of modules should implements this interface
 * </p>
 * 
 * @author osmos
 * @date 2015年6月12日
 */
public interface Module {

	// Constants & Enums

	/** this top dir holds all maked fiels for test environment */
	public final static String MAKE_DIR = "/sites";

	/** this top dir holds all maked fiels for product environment */
	public final static String PUB_DIR = "/public";

	public final static String USER_DIR = "/resource/custom";
	
	/** location for upload headerPic */
	public final static String IMAGE_UPLOAD_DIR = "/resource/attachment/images";

	/** size for save samll and big headerPic */
	public final static Integer SMALL_PIC_WIDTH = 165;
	public final static Integer SAMLL_PIC_HEIGHT = 105;
	public final static Integer BIG_PIC_WIDTH = 550;
	public final static Integer BIG_PIC_HEIGHT = 350;

	/** extension name enum of the module file */
	public enum ExtensionName {
		html, shtml, css, js, json;
	}

	/** type enum of the module */
	public enum ModuleType {
		SITE, COLUMN, SUBJECT, CUSTOM
	}

	// Interface Methods

	/**
	 * get title of module
	 * 
	 * @return title
	 */
	public String getTitle();

	/**
	 * get description of module
	 * 
	 * @return description
	 */
	public String getDescription();

	/**
	 * get directory name of module
	 * 
	 * @return directory name
	 */
	public String getDirName();

	/**
	 * get filename with no prefix of path and no postfix of extension name
	 * 
	 * @return file name of module
	 */
	public String getFileName();

	/**
	 * get extension name of file
	 * 
	 * @return extension name
	 */
	public String getExtensionName();

	/**
	 * get module type
	 * 
	 * @return module type
	 */
	public String getModuleType();

	/**
	 * get module file's test environment url
	 * 
	 * @return the test environment url
	 */
	public String getTestUrl();

	/**
	 * get module file's test product environment url
	 * 
	 * @return the product environment url
	 */
	public String getProductUrl();

	/**
	 * get absolute webpath of the module dir to the root web context
	 * 
	 * @return absolute webpath
	 */
	public String getDirWebpath();

	/**
	 * get absolute diskpath of the module dir
	 * 
	 * @return absolute diskpath
	 */
	public String getDirDiskpath();

	/**
	 * get absolute webpath of the module file to the root web context
	 * 
	 * @return absolute webpath
	 */
	public String getFileWebpath();

	/**
	 * get absolute diskpath of the module file
	 * 
	 * @return absolute diskpath
	 */
	public String getFileDiskpath();

	/**
	 * get absolute webpath of the module file to the root web context
	 * 
	 * @param pageth
	 * 
	 * @return absolute webpath
	 */
	public String getFileWebpath(int pageth);

	/**
	 * get absolute diskpath of the module file
	 * 
	 * @param pageth
	 * @return absolute diskpath
	 */
	public String getFileDiskpath(int pageth);
}
