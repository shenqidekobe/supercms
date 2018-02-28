package com.dw.suppercms.produce.data;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.produce.rule.VariableDefine;

/**
 * 数据转换器
 * @author kobe
 * @version 1.0
 * */
public class DataRecordTransformConvetor {
	
	
	/**
	 * dataRecord转换为动态bean
	 * @param DataRecord
	 * @return DynaBean
	 * */
	@SuppressWarnings("unchecked")
	public static DynaBean dataRecordTransformDynaBean(DataRecord dataRecord,Column column){
		//LazyDynaClass lazyDynaClass=new LazyDynaClass();lazyDynaClass.add("dataId", String.class);
		DynaBean dynaBean=new LazyDynaBean();
		Assert.notNull(dataRecord);
		try {
			Field[] fields=dataRecord.getClass().getDeclaredFields();
			for(Field field:fields){
				String fieldName=field.getName();
				field.setAccessible(true);//因为属性为private，修改访问权限
				if(fieldName.equals("serialVersionUID")){
					continue;
				}else if(fieldName.equals("extendFiledMap")){
					//获取扩展字段map中的字段和值
					Map<String, Object> map=(Map<String, Object>) field.get(dataRecord);
					if(map!=null&&!map.isEmpty()){
						for(String key:map.keySet()){
							dynaBean.set(key, map.get(key));
						}
					}
					
				}else{
					Object objVal= field.get(dataRecord);
					if(field.getType().equals(Date.class)&&objVal!=null){
						objVal=DateFormatUtils.format((Date)objVal, VariableDefine.date_format_second);
					}
					dynaBean.set(fieldName,objVal);
				}
			}
			if(column!=null){
				String href=dataRecord.getUrlRef();
				if(StringUtils.isEmpty(href)){
					href=column.getContentWebPath(dataRecord.getId(), new Timestamp(dataRecord.getTime().getTime()));
				}
				dynaBean.set(VariableDefine.pading_href,href);//设置路径地址
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dynaBean;
	}
	
	/**
	 * dataMap转换为dataRecord
	 * */
	public static DataRecord dataMapToDataRecord(Map<String,Object> dataMap,String picDomain){
		DataRecord dataRecord=new DataRecord();
		try {
			if(!dataMap.isEmpty()){
				Map<String, Object> extendFiledMap=new HashMap<>();
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for(String key:dataMap.keySet()){
					Object value=dataMap.get(key);
					String obj=value==null?"":value.toString();
					switch (key) {
					case "id":
						dataRecord.setId(obj);
						break;
					case "title":
						dataRecord.setTitle(obj);
						break;
					case "shortTitle":
						dataRecord.setShortTitle(obj);
						break;
					case "urlRef":
						dataRecord.setUrlRef(obj);
						break;
					case "headerPicRef":
						dataRecord.setHeaderPicRef(obj);
						break;
					case "source":
						dataRecord.setFrom(obj);
						break;
					case "author":
						dataRecord.setWriter(obj);
						break;
					case "introduce":
						dataRecord.setIntro(obj);
						break;
					case "content":
						dataRecord.setContent(obj);
						break;
					case "editor":
						dataRecord.setEditor(obj);
						break;
					case "datasource_id":
						dataRecord.setDataSourceId(Long.parseLong(obj));
						break;
					case "model_id":
						dataRecord.setModelId(Long.parseLong(obj));
						break;
					case "create_time":
						Date createTime=value==null?null:format.parse(obj);
						dataRecord.setTime(createTime);
						break;
					case "publish_time":
						Date publishTime=value==null?null:format.parse(obj);
						dataRecord.setPublishTime(publishTime);
						break;
					default:
						if(key.endsWith("Pic")){
							value=picDomain+value;
						}
						extendFiledMap.put(key, value);
						break;
					}
				}
				dataRecord.setExtendFiledMap(extendFiledMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataRecord;
	}
	
	public static void main(String[] args) {
		DataRecord dataRecord=new DataRecord();
		 Map<String, Object> extendFiledMap=new HashMap<>();
		 extendFiledMap.put("max", 100);
		 extendFiledMap.put("min", 18);
		 extendFiledMap.put("jpxsb", "maxu");
		 
		dataRecord.setId("100AB");
		dataRecord.setTitle("NBA_KOBE_BRYANT");
		dataRecord.setTime(new Date());
		dataRecord.setExtendFiledMap(extendFiledMap);
		
		DynaBean dynaBean=dataRecordTransformDynaBean(dataRecord,null);
		System.out.println(dynaBean.get("createTime"));
		
		String str="www.baidu.com   http://a/vba.dd?sdsd=12";
		
		
		System.out.println(StringUtils.substringBefore(str, "http").trim());
		
	}

}
