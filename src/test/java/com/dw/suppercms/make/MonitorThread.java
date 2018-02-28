package com.dw.suppercms.make;

import javax.annotation.Resource;

import org.junit.Test;

import com.dw.suppercms.produce.executor.MakeFileThreadPoolMonitor;
import com.dw.suppercms.infrastructure.config.TestConfig;

public class MonitorThread extends TestConfig{

	
	@Resource
	private MakeFileThreadPoolMonitor makeFileThreadPoolMonitor;
	
	
	@Test
	public void monitorThreadPool(){
		makeFileThreadPoolMonitor.start();
	}

	
}
