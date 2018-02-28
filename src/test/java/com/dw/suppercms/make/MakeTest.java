package com.dw.suppercms.make;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.suppercms.infrastructure.config.TestConfig;
import com.dw.suppercms.produce.MakeFileService;

public class MakeTest extends TestConfig {
	
	@Autowired
	MakeFileService makeFileService;
	
	@Test
	public void makeIndex() {
		long a=System.currentTimeMillis();
		makeFileService.makeAllFile();
		System.out.println("\r<br>执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
	}

}
