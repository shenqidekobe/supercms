package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.suppercms.application.plugin.HitsService;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.infrastructure.utils.TopData;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 点击量数据 排行榜指令
 * @author kobe
 * */
@SuppressWarnings({ "rawtypes" })
@Service
public class HitsTopDirective extends BaseDirective{
	

	public static final String SITE_ID = "sid";   //站点ID参数
	public static final String COLUMN_ID = "cid"; //栏目ID参数
	public static final String MANU_ID = "organ"; //查询单位点赞排行榜
	public static final String TOP_NUM = "topNum";//排行多少天参数
	public static final String PARAM_PAGE_SIZE = "pageSize";//查询多少条参数
	public static final String PARAM_DIR = "dir";   //排序方式(按点赞数量排序)
	
	@Resource
	private HitsService hitsService;
	
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] models,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		try {
			List<TopData> list=getList(env,params);
			env.setVariable("list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
			env.getConfiguration().setSharedVariable("list", list);
			if(body!=null){
				body.render(env.getOut());
			}
		} catch (Exception e) {
			String desc=String.format("点击量排行榜指令解析错误,params=%s", params);
			saveDirectiveParseErrorLog(desc, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public List<TopData> getList(Environment env,Map params) throws TemplateModelException {
		Object dirParamObj = params.get(PARAM_DIR);
		String dir = dirParamObj == null ? null : dirParamObj.toString();
		
		Object pageSizeParamObj = params.get(PARAM_PAGE_SIZE);
		Integer pageSizeParam = pageSizeParamObj==null ? null : Integer.valueOf(pageSizeParamObj.toString());
		Integer pageSize = (pageSizeParam == null ? DEFAULT_PAGE_SIZE : pageSizeParam);

		Object siteParamObj = params.get(SITE_ID);
		String siteId = siteParamObj == null ? null : (siteParamObj.toString());
		
		Object columnParamObj = params.get(COLUMN_ID);
		String columnId =  (columnParamObj == null ? null :  (columnParamObj.toString()));
		
		Object manuParamObj = params.get(MANU_ID);
		String organ =  (manuParamObj == null ? "" : manuParamObj.toString());
		boolean isOrgan=false;
		if("Y".equals(organ)){
			isOrgan=true;//如是单位排行榜,则填入一个稿件ID即可
		}
		
		Object topParamObj = params.get(TOP_NUM);
		Integer topNum=1;
		try {
			topNum = topParamObj == null ? 0 : Integer.parseInt(topParamObj.toString());
		} catch (Exception e) {}
		Pager pager = new Pager();
		pager.setDir(dir==null?"desc":dir);
		pager.setPageSize(pageSize);
		
		return this.hitsService.findHitsByTop(siteId, columnId,isOrgan,topNum, pager);
	}
	

}
