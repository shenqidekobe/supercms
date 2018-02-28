package com.dw.suppercms.infrastructure.web.plugin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.plugin.HitsService;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 点击量处理器
 * */
@RestController
@RequestMapping("/hits")
public class HitsController extends BaseController{
	
	
	@Resource
	private HitsService hitsService;
	
	/**
	 * 记录点击量
	 * */
	@RequestMapping(value="/save",method={RequestMethod.GET})
	public JSONPObject save(String callback,Long siteId,String link,Long columnId,Long recordId,HttpServletRequest request){
		String result=null;
		try {
			if(columnId!=null&&recordId!=null){
				String ip=CommonsUtil.getIPAddress(request);
				Integer num=this.hitsService.saveHits(siteId,columnId, link, ip, recordId);
				result=num+"";
			}else{
				result="-2";
			}
		} catch (Exception e) {
			result="-1";
		}
		return new JSONPObject(callback, result);
	}
	
	/**
	 * 获取文章的点击量
	 * */
	@RequestMapping(value="/num",method={RequestMethod.GET})
	public JSONPObject num(String callback,Long columnId,Long recordId)throws Exception{
		String result=null;
		try {
			if(columnId!=null&&recordId!=null){
				Integer num=this.hitsService.findHitsCountByColumnIdAndRecordId(columnId, recordId);
				result=num+"";
			}else{
				result="-2";
			}
		} catch (Exception e) {
			result="-1";
		}
		return new JSONPObject(callback, result);
	}

}
