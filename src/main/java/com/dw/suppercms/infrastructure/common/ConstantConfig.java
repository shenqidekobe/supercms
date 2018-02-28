package com.dw.suppercms.infrastructure.common;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 系统运行参数定义
 * */
@Service
@Data
public class ConstantConfig {
	
	@Value("${host}")
	public String host;//服务器地址
	
	@Value("${website_publish_url}")
	private String websitePublishUrl;//网站发布地址
	
	@Value("${website_publish_img_server}")
	private String websitePublishImgServer;//发布网站的图片服务
	
	@Value("${content_intro_length}")
	private String contentIntroLength;//内容转换简介的截取长度
	
	@Value("${is_open_auto_make_task}")
	public String isOpenAutoMakeTask;//是否开始自动生成任务
	
	@Value("${make_file_sleep_times}")
	public String makeFileSleepTimes;//生成线程监控间隔时间
	
	@Value("${job_task_sleep_times}")
	public String jobTaskSleepTimes;//任务调度监控间隔时间
	
	@Value("${audit_lock_time}")
	public String auditLockTime;//审稿锁定时间的间隔
	
	@Value("${manuscript_short_title_ids}")
	public String shortTitleIds;//投稿需要短标题的数据源ID
	
	@Value("${data_index_siteIds}")
	public String dataIndexSiteIds;//需要索引的站点ID
	
}
