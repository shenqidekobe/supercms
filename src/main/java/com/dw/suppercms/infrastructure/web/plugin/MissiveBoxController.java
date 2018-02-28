package com.dw.suppercms.infrastructure.web.plugin;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dw.suppercms.application.plugin.MissiveBoxService;
import com.dw.suppercms.domain.plugin.MissiveBoxInfo;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 稿件箱控制器
 * */
@RestController
@RequestMapping("/missiveBox")
public class MissiveBoxController extends BaseController{
	
	@Resource
	private MissiveBoxService missiveBoxService;
	
	@RequestMapping(value="/save",method={RequestMethod.GET})
	public JSONPObject save(MissiveBoxInfo obj){
		int result=0;
		try {
			this.missiveBoxService.saveMailBox(obj);
		} catch (Exception e) {
			result=1;
		}
		return new JSONPObject("callback", result);
	}

}
