package com.dw.suppercms.application.plugin;

import com.dw.suppercms.domain.plugin.MissiveBoxInfo;


/**
 * 信箱服务接口(网页投稿)
 * */
public interface MissiveBoxService {
	
	/**
	 * 保存信件内容
	 * */
	public void saveMailBox(MissiveBoxInfo obj);

}
