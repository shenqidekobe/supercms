package com.dw.suppercms.infrastructure.web.plugin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.plugin.PraiseService;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 点赞处理器
 * */
@RestController
@RequestMapping("/praise")
public class PraiseController extends BaseController{
	
	
	@Resource
	private PraiseService praiseService;
	
	/**
	 * 记录点赞
	 * 采用jsonp的方式进行返回，jsonp原生不支持post
	 * 有关jsonp的原理参考：http://www.nowamagic.net/librarys/veda/detail/224
	 * */
	@RequestMapping(value="/save",method={RequestMethod.GET})
	public JSONPObject save(String callback, Long siteId,String link,Long columnId,Long recordId,HttpServletRequest request){
		String result=null;
		try {
			if(columnId!=null&&recordId!=null){
				String ip=CommonsUtil.getIPAddress(request);
				Integer num=this.praiseService.savePraise(siteId,columnId, link, ip, recordId);
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
	 * 获取文章的点赞量
	 * */
	@RequestMapping(value="/num",method={RequestMethod.GET})
	public JSONPObject num(String callback,Long columnId,Long recordId)throws Exception{
		String result=null;
		try {
			if(recordId!=null){
				Integer num=0;
				if(columnId!=null){
					num=this.praiseService.findPraiseCountByColumnIdAndRecordId(columnId, recordId);
				}else{
					num=this.praiseService.findPraiseCountByRecordId(recordId);
				}
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
