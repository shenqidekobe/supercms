package com.dw.suppercms.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.suppercms.application.system.SysSortService;
import com.dw.suppercms.domain.system.SysSortInfo;
import com.dw.suppercms.domain.system.SysSortInfo.SORT_TYPE;
import com.dw.suppercms.infrastructure.config.TestConfig;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

public class SortTest extends TestConfig{
	
	@Autowired
	private SysSortService sysSortService;
	
	@Test
	public void sortTree(){
		List<SysSortInfo> list=this.sysSortService.findRootSortByType(SORT_TYPE.template_snippet.toString());
		List<SysSortInfo> asList=new ArrayList<SysSortInfo>();
		for(SysSortInfo sort:list){
			print(asList,sort);
		}
		
		JSONSerializer serializer = new JSONSerializer();
		serializer.exclude("*.class")
				.transform(new DateTransformer("yyyy-MM-dd HH:mm:ss"), Date.class);
		String json=serializer.deepSerialize(list);
		System.out.println("json = "+json);
	}
	
	private void print(List<SysSortInfo> asList,SysSortInfo sort) {  
        System.out.println(sort.getSortName());
        sort.setChildren(sort.getChildren());
        asList.add(sort);
        for(SysSortInfo child: sort.getChildren()){  
        	print(asList, child);
        } 
	}
}
