package com.dw.suppercms.produce.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dw.suppercms.infrastructure.common.ConstantConfig;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/**
 * 视频播放指令
 * */
@Service
@SuppressWarnings("rawtypes")
public class VideoDirective extends BaseDirective {
	
	@Resource
	private ConstantConfig constantConfig;

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateHashModel obj = (TemplateHashModel)env.getVariable("obj");
		try {
			String imgDomain=constantConfig.getWebsitePublishImgServer();
			String videos = obj.get("videos").toString();
			String cover = obj.get("cover").toString();
			String length = parseLength(obj.get("length").toString());
			StringBuffer sb = new StringBuffer();
			sb.append("<link href='/player1/video-js.css' rel='stylesheet' type='text/css'>");
			sb.append("<script src='"+imgDomain+"/plugin/player/video.js'></script>");
			sb.append("<script src='"+imgDomain+"/plugin/player/myvideo.js'></script>");
			sb.append("<script>");
			sb.append("var xjs={};");
			sb.append("var videoUrls='" + videos + "';");
			sb.append("var videoTotalTimes=" + length + ";");
			sb.append("</script>");
			sb.append("<video id='video' class='video-js vjs-default-skin'  poster='"+cover+"'></video>");
			env.getOut().write(sb.toString());
			if (body == null) {
				body = new TemplateDirectiveBody() {
					@Override
					public void render(Writer writer) throws TemplateException, IOException {}
				};
			}
			body.render(env.getOut());
		} catch (Exception e) {
			saveDirectiveParseErrorLog("视频指令解析错误", e.getMessage());
		}
	}
	
	private String parseLength(String length){
		try {
			int hour = Integer.parseInt(StringUtils.substringBefore(length, "时"));
			int minute = Integer.parseInt(StringUtils.substringBetween(length, "时", "分"));
			return String.valueOf((hour * 60 * 60 + minute * 60));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "";
		}
	}
}
