package com.dw.suppercms.produce.rule;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.dw.suppercms.application.data.DataService;
import com.dw.suppercms.application.data.DatasourceService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.MakeFileService;

/**
 * cms文件处理工具类
 * 
 * @date 2014-11-07
 * */
@Service("cmsFileUtils")
public class CmsFileUtils {

	private static Logger log = Logger.getLogger(CmsFileUtils.class);

	@Resource
	private MakeFileService makeFileService;
	
	@Resource
	private ProduceFileService produceFileService;
	
	@Resource
	private DataService dataService;
	
	@Resource
	private DatasourceService datasourceService;
	
	/**
	 * 删除最终页面文件
	 * 
	 * @param recordId:数据ID
	 * */
	public boolean removeFileFinalPage(final Long recordId,final Long dataSourceId) {
		boolean flag=false;
		try {
			Assert.notNull(recordId);
			Map<String, Object> dataMap=dataService.retrieve(dataSourceId, recordId);
			Assert.notNull(dataMap);
			Date createTime = (Date) dataMap.get("create_time");
			Datasource ds=datasourceService.retrieve(dataSourceId);
			final Set<Column> columns =ds.getColumns();
			Assert.notNull(columns);
			for (Column column : columns) {
				String path =column.getContentFilePath(recordId.toString(), new Timestamp(createTime.getTime()));
				log.info("系统开始删除最终页面文件:" + path);
				FileUtils.deleteQuietly(new File(path));
				column.setMakeListState(false);//重新生成列表
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					makeFileService.makeListFile(new ArrayList<>(columns),null);// 删除最终页，调用生成栏目列表页和首页的接口刷新列表页
				}
			}).start();
			flag=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除站点下所有文件
	 * 
	 * @param siteId:站点ID
	 * */
	public void removeFilePagesSite(Long siteId) {
		Assert.notNull(siteId);
		Site site = this.produceFileService.findSiteById(siteId);
		Assert.notNull(site);
		String fullPath = site.getDirDiskpath();
		try {
			log.info("系统开始删除站点：" + site.getTitle() + "，" + fullPath + "下所有文件.");
			FileUtils.deleteDirectory(new File(fullPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除栏目下所有文件
	 * 
	 * @param columnId:栏目ID
	 * */
	public void removeFilePagesColumn(Long columnId) {
		Assert.notNull(columnId);
		Column column = this.produceFileService.findColumnById(columnId);
		Assert.notNull(column);
		String fullPath = column.getDirDiskpath();
		try {
			log.info("系统开始删除栏目：" + column.getTitle() + "，" + fullPath + "下所有文件.");
			FileUtils.deleteDirectory(new File(fullPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步文件夹
	 * */
	public static void sysncDirs(String src, String desc) {
		Process proc;
		try {
			String[] command = new String[] { "/usr/bin/rsync", "-vzrtopg", "--del", src + "/", desc + "/" };
			proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
			log.info("文件夹："+src+" 同步到目标文件夹："+desc);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	 
}
