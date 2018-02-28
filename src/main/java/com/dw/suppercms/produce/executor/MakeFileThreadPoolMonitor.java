package com.dw.suppercms.produce.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dw.suppercms.infrastructure.common.ConstantConfig;

/**
 * 生成文件线程池监控类，应用启动开始执行
 * 此后每间隔sleep_times毫秒重复执行
 * @author kobe
 * @version 1.0
 * */
@Service
public class MakeFileThreadPoolMonitor extends Thread{
	
	private Logger logger=LoggerFactory.getLogger(MakeFileThreadPoolMonitor.class);
	
	private static volatile boolean is_stop=false;
	
	private static long sleep_times=1000*60*15;
	
	@Resource
	private  ExecutorService makeIndexExecutorService;
	@Resource
	private  ExecutorService makeListExecutorService;
	@Resource
	private  ExecutorService makeContentExecutorService;
	@Resource
	private  ExecutorService makeCustomExecutorService;
	
	@Resource
	private ConstantConfig constantConfig;
	
	@PostConstruct
	public void onStart(){
		logger.info("生成文件线程池监控线程开始启动...");
		sleep_times=Long.parseLong(constantConfig.getMakeFileSleepTimes());
		this.setName("makefile-executor-monitor");
		this.setDaemon(true);
		this.start();
	}
	
	@PreDestroy
	public void onDestory(){
		this.interrupt();
	}
	
	
	@Override
	public void run(){
		while(!is_stop){
			try {
				Thread.sleep(sleep_times);
			} catch (InterruptedException e) {
			}
			logger.info(printThreadState().toString());
		}
	}
	
	/**
	 * 打印各个线程池的状态和信息
	 * */
	private StringBuffer printThreadState(){
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(System.lineSeparator()+"**********************生成首页任务线程池***********************");
		strBuff.append(System.lineSeparator()+"* 当前线程池大小 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getPoolSize()+String.format("%42s", "*"));
		strBuff.append(System.lineSeparator()+"* - 核心线程池大小 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getCorePoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 最大线程池大小 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getMaximumPoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 执行任务的线程数 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getActiveCount()+String.format("%39s", "*"));
		strBuff.append(System.lineSeparator()+"* - 已执行完成的任务数 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getCompletedTaskCount()+String.format("%37s", "*"));
		strBuff.append(System.lineSeparator()+"* - 曾计划执行的总任务数 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).getTaskCount()+String.format("%36s", "*"));
		strBuff.append(System.lineSeparator()+"* - 是否已关闭 : ").append(((ThreadPoolExecutor) makeIndexExecutorService).isTerminated()+String.format("%39s", "*"));
		
		strBuff.append(System.lineSeparator()+"**********************生成列表任务线程池***********************");
		strBuff.append(System.lineSeparator()+"* 当前线程池大小 : ").append(((ThreadPoolExecutor) makeListExecutorService).getPoolSize()+String.format("%42s", "*"));
		strBuff.append(System.lineSeparator()+"* - 核心线程池大小 : ").append(((ThreadPoolExecutor) makeListExecutorService).getCorePoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 最大线程池大小 : ").append(((ThreadPoolExecutor) makeListExecutorService).getMaximumPoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 执行任务的线程数 : ").append(((ThreadPoolExecutor) makeListExecutorService).getActiveCount()+String.format("%39s", "*"));
		strBuff.append(System.lineSeparator()+"* - 已执行完成的任务数 : ").append(((ThreadPoolExecutor) makeListExecutorService).getCompletedTaskCount()+String.format("%37s", "*"));
		strBuff.append(System.lineSeparator()+"* - 曾计划执行的总任务数 : ").append(((ThreadPoolExecutor) makeListExecutorService).getTaskCount()+String.format("%36s", "*"));
		strBuff.append(System.lineSeparator()+"* - 是否已关闭 : ").append(((ThreadPoolExecutor) makeListExecutorService).isTerminated()+String.format("%39s", "*"));
		
		strBuff.append(System.lineSeparator()+"**********************生成最终页任务线程池**********************");
		strBuff.append(System.lineSeparator()+"* 当前线程池大小 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getPoolSize()+String.format("%42s", "*"));
		strBuff.append(System.lineSeparator()+"* - 核心线程池大小 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getCorePoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 最大线程池大小 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getMaximumPoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 执行任务的线程数 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getActiveCount()+String.format("%39s", "*"));
		strBuff.append(System.lineSeparator()+"* - 已执行完成的任务数 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getCompletedTaskCount()+String.format("%37s", "*"));
		strBuff.append(System.lineSeparator()+"* - 曾计划执行的总任务数 : ").append(((ThreadPoolExecutor) makeContentExecutorService).getTaskCount()+String.format("%36s", "*"));
		strBuff.append(System.lineSeparator()+"* - 是否已关闭 : ").append(((ThreadPoolExecutor) makeContentExecutorService).isTerminated()+String.format("%39s", "*"));
		
		strBuff.append(System.lineSeparator()+"**********************生成自定义页任务线程池*********************");
		strBuff.append(System.lineSeparator()+"* 当前线程池大小 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getPoolSize()+String.format("%42s", "*"));
		strBuff.append(System.lineSeparator()+"* - 核心线程池大小 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getCorePoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 最大线程池大小 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getMaximumPoolSize()+String.format("%40s", "*"));
		strBuff.append(System.lineSeparator()+"* - 执行任务的线程数 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getActiveCount()+String.format("%39s", "*"));
		strBuff.append(System.lineSeparator()+"* - 已执行完成的任务数 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getCompletedTaskCount()+String.format("%37s", "*"));
		strBuff.append(System.lineSeparator()+"* - 曾计划执行的总任务数 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).getTaskCount()+String.format("%36s", "*"));
		strBuff.append(System.lineSeparator()+"* - 是否已关闭 : ").append(((ThreadPoolExecutor) makeCustomExecutorService).isTerminated()+String.format("%39s", "*"));
		strBuff.append(System.lineSeparator()+"**************************************************************");
		
		return strBuff;
	}

	
	
	
}
