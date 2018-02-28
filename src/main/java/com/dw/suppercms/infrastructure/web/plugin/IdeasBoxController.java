package com.dw.suppercms.infrastructure.web.plugin;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.plugin.IdeasBoxService;
import com.dw.suppercms.domain.plugin.IdeasBoxInfo;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 意见箱控制器
 * */
@RestController
@RequestMapping("/ideasBox")
public class IdeasBoxController extends BaseController{
	
	@Resource
	private IdeasBoxService ideasBoxService;
	
	@RequestMapping(value="/save",method={RequestMethod.GET})
	public JSONPObject save(IdeasBoxInfo obj){
		int result=0;
		try {
			this.ideasBoxService.saveIdeasBox(obj);
		} catch (Exception e) {
			result=1;
		}
		return new JSONPObject("callback", result);
	}

}
