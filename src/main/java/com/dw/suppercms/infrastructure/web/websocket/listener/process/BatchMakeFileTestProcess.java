package com.dw.suppercms.infrastructure.web.websocket.listener.process;

import org.springframework.stereotype.Service;

import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;
import com.dw.suppercms.infrastructure.web.websocket.dto.MakeProcess;
import com.dw.suppercms.infrastructure.web.websocket.listener.BatchMakeFileScheduleListener;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;

import flexjson.JSONSerializer;

/**
 * 测试批量更新
 * */
@Service
public class BatchMakeFileTestProcess extends BatchMakeFileScheduleListener{
	
	@Override
	protected void execute(BatchTask task) {
		MakeProcess process = null;
		int c=10;
		int temp=0;
		int defaultEvery=800;
		int total=c*defaultEvery+c;
		boolean flag=true;
		for (int i = 0; i < c; i++) {
			flag=true;
			String columnName ="这是一个神奇的网站 columnID="+i;
			int size=100*(i+1);
			for (int j = 1; j <= size; j++) {
				temp++;
				process = new MakeProcess();
				int percent=0;
				if(size<=defaultEvery){
					if(flag){
						temp+=defaultEvery-size;
						flag=false;
					}
					percent = Math.round((temp) * 1f / total * 100);
				}else{
					if(flag){
						total+=size-defaultEvery;
						flag=false;
					}
					percent = Math.round((temp) * 1f / total * 100);
				}
				Long id = 100L;
				String name ="这是一个神奇的网站"+j;
				process.setType(MakeType.SELF_INCREMENT);
				String title = String.format("%s/%s [%s] %s <br/>&nbsp;&nbsp;&nbsp;&nbsp; %s/%s [%s] %s", i, c,
						id, columnName, j, size*c, j, name);
				process.setTitle(title);
				process.setPercent(percent);
				process.setError("NULLEXSKJFSHFSDJK ..ASDJSDJ");
				sendMesage(new JSONSerializer().serialize(process));
			}
		}
		for (int i = 0; i < c; i++) {
			String columnName ="这是一个神奇的网站 columnID="+i;
			process = new MakeProcess();
			int percent = Math.round((temp+i) * 1f / (total)* 100);
			Long id = 100L;
			process.setType(MakeType.SELF_INCREMENT);
			String title = String.format("%s/%s [%s] %s", i, c,id, columnName);
			process.setTitle(title);
			process.setPercent(percent);
			process.setError("NULLEXSKJFSHFSDJK ..ASDJSDJ");
			if(i==9){
				process.setIsComplete(true);
			}
			sendMesage(new JSONSerializer().serialize(process));
		}
	}
	
	@Override
	public boolean supportsMakeType(MakeType makeType) {
		return MakeType.TEST.equals(makeType);
	}

}
