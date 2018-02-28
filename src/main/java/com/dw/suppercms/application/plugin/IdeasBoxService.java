package com.dw.suppercms.application.plugin;

import com.dw.suppercms.domain.plugin.IdeasBoxInfo;


/**
 * 意见箱服务接口
 * */
public interface IdeasBoxService {
	
	/**
	 * 保存意见内容
	 * */
	public void saveIdeasBox(IdeasBoxInfo obj);

}
