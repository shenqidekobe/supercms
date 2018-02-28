package com.dw.suppercms.domain.system;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务日志
 * */
@Data
@Entity
@EqualsAndHashCode(callSuper = false,of={"id"})
public class JobTaskLogInfo implements Serializable{
	
	private static final long serialVersionUID = 7234972680888183017L;
	
	public enum TASK_RESULT{
		SUCCESS,FAIL
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Long taskId;//任务ID
	
	private String consuming;//任务耗时
	
	@Enumerated(EnumType.STRING)
	private TASK_RESULT exeResult;//任务执行结果
	
	@Lob
	private String exeMessage;//任务执行信息
	
	private Date exeTime;//任务执行时间点
	
	@ManyToOne
	@JoinColumn(name = "taskId", insertable = false, updatable = false)
	private JobTaskInfo jobTask;

}
