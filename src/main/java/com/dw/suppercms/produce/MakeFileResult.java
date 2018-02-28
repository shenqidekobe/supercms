package com.dw.suppercms.produce;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.cxf.common.util.StringUtils;

import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Custom;
import com.dw.suppercms.domain.modules.Site;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;

/**
 * 生成文件结果
 * */
public class MakeFileResult implements Serializable{
	
	
	private static final long serialVersionUID = 1255399566205817030L;
	
	//生成错误代码{正常运行、系统错误、运行时异常、模版错误、解析指令异常、解析方法异常}
	public enum MAKE_ERROR_CODE{
		NORMAL,ERROR,RUNTIME_EXCEPTION,TEMPLATE_ERROR,PARSE_DIRECTIVE_EXCEPTION,PARSE_METHOD_EXCEPTION,UNKNOWN
	}
	
	public MakeFileResult(){}
	
	
	
	public MakeFileResult(MakeEventType makeType, MAKE_ERROR_CODE makeErrorCode,
			String makeErrorMsg, String makeDesc, Date startMakeTime,
			int makeCount, int successCount, int failCount,
			MAKE_RESULT makeResult) {
		this.makeType = makeType;
		this.makeErrorCode = makeErrorCode;
		this.makeErrorMsg = makeErrorMsg;
		this.makeDesc = makeDesc;
		this.startMakeTime = startMakeTime;
		this.makeCount = makeCount;
		this.successCount = successCount;
		this.failCount = failCount;
		this.makeResult = makeResult;
	}



	public MakeFileResult(MakeEventType makeType,MAKE_RESULT makeResult,int makeCount,int successCount,int failCount){
		this.makeType=makeType;
		this.makeResult=makeResult;
		this.makeCount=makeCount;
		this.successCount=successCount;
		this.failCount=failCount;
	}

	//生成结果
	public enum MAKE_RESULT{
		SUCCESS,FAIL,PART_SUCCESS
	}
	
	@Setter
	@Getter
	private Long id; //ID{站点、栏目、自定义页}
	
	@Setter
	@Getter
	private String title;//标题{站点、栏目、自定义页}
	
	@Setter
	@Getter
	private String dataId;//数据ID，
	
	@Setter
	@Getter
	private String dataTitle;//数据标题,
	
	@Setter
	@Getter
	private MakeEventType makeType;//执行的类型
	
	@Setter
	@Getter
	private MAKE_ERROR_CODE makeErrorCode=MAKE_ERROR_CODE.NORMAL;//执行错误代码
	
	@Setter
	@Getter
	private String makeErrorMsg;//执行错误信息
	
	@Setter
	@Getter
	private String makeDesc;//执行任务描述信息
	
	@Setter
	@Getter
	private Date startMakeTime;//开始执行的时间
	
	@Setter
	@Getter
	private int makeCount; //生成任务数
	
	@Setter
	@Getter
	private int successCount; //成功数量
	
	@Setter
	@Getter
	private int failCount;  //失败数量
	
	@Setter
	@Getter
	private Column column;//栏目
	
	@Setter
	@Getter
    private Site site;//站点对象
	
	@Setter
	@Getter
	private Custom custom;//自定义页对象
	
	@Setter
	@Getter
	private String filePath;//文件地址
	
	@Setter
	@Getter
	private Set<Site> successSites;//成功执行的栏目
	@Setter
	@Getter
	private Set<Column> successColumns;//成功执行的栏目
	@Setter
	@Getter
	private Set<Custom> successCustoms;//成功执行的栏目
	
	@Setter
	private MAKE_RESULT makeResult=MAKE_RESULT.SUCCESS;//执行结果

	public MAKE_RESULT getMakeResult() {
		return StringUtils.isEmpty(getMakeErrorMsg())?makeResult:MAKE_RESULT.FAIL;
	}
	
	

}
