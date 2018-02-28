package com.dw.suppercms.infrastructure.utils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.dw.suppercms.domain.security.User;

/**
 * 公用帮助类
 * */
public class CommonsUtil {
	
	/**
	 * 获取shiro登录用户标识
	 * */
	public static User getPrincipal() {
		try {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				User principal = (User) subject.getPrincipal();
				return principal;
			}
		} catch (Exception e) {
		}
		return User.newOf();
	}
	
	/**
	 * 获取请求IP
	 * */
	public static String getIPAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * sql处理，给字符串加上引号
	 * */
	public static String characterAddQuotes(String strs){
		if(StringUtils.isEmpty(strs))return "";
		if(strs.indexOf(",")==-1)return "'"+strs+"'";
		String[] datas=StringUtils.split(strs, ",");
		String result="";
		for(String data:datas){
			result+="'"+data+"',";
		}
		result=StringUtils.chop(result);
		return result;
	}
	
	public static <T> String collectionToInArgs(Collection<T> collection) {
		StringBuffer inArgs = new StringBuffer();
		Iterator<T> iter = collection.iterator();
		while (iter.hasNext()) {
			T t = iter.next();
			String comma = (inArgs.length() == 0 ? "" : ",");
			String singleQuote = t instanceof Number ? "" : "'";
			inArgs.append(comma + singleQuote + t + singleQuote);
		}
		return inArgs.toString();
	}

	public static String formatDateToString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	/**
	 * 毫秒转换为时分秒
	 * */
	public static String formateTimes(Long ms) {
		Integer ss = 1000;
		Integer mi = ss * 60;
		Integer hh = mi * 60;
		Integer dd = hh * 24;

		Long day = ms / dd;
		Long hour = (ms - day * dd) / hh;
		Long minute = (ms - day * dd - hour * hh) / mi;
		Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}
	
	 /**
     * 从正文中截取cutLength长度的简介
     * 正文过滤html标签
     * */
    public static String getIntroFromContent(String content,Integer cutLength){
    	String intro="";
    	if(StringUtils.isEmpty(content)){
    		return intro;
    	}
    	intro = content.replaceAll("<(?i)script[^>]*>[\\d\\D]*?</(?i)script>", ""); // 防止script脚本注入
	    intro = intro.replaceAll("<([^>]*)>", "").replaceAll("\\s*", "");
	    if(intro.length()>cutLength){
		   intro=intro.substring(0,cutLength);
	    }
	    return intro;
    }
    
    /**
     * 清除html标记
     * */
    public static String delHTMLTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
         
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //过滤script标签 
         
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //过滤style标签 
         
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //过滤html标签 

        return StringUtils.trim(htmlStr); //返回文本字符	串 
    }
}
