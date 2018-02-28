package com.dw.suppercms.domain.system;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 生成日志对象
 * */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class ProduceLogInfo extends IdentifiedEntity{

	private static final long serialVersionUID = 5224689007684646176L;
	
	private Long userId;  //执行者
	
	private String produceType;//生成的任务类型{index,(content,list),list,content,custom}
	
	private Date produceDate;//生成时间
	
	private String produceDesc;//生成描述
	
	private String produceResult;//生成结果{SUCCESS,FAIL,PART_SUCCESS}
	
	@Lob
	private String produceResultMsg;//生成结果日志信息{错误信息}
	
	private int produceCount;//生成条数
	
	private int produceSuccessCount;//生成成功数目
	
	private int produceFailCount;//生成失败数目
	
	private Long taskId;//任务ID{冗余字段，如果是任务执行的生成就会产生taskID，非任务的生成则为空}

}
