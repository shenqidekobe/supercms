package com.dw.suppercms.domain.plugin;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点击量历史记录
 * @kobe
 * */
@Entity
@Table(name="t_hits_history")
@Data
@EqualsAndHashCode(callSuper = false)
public class HitsHistory implements Serializable{
	
	private static final long serialVersionUID = -7279561956749934950L;

	@Id
	@GeneratedValue
	private Long id;//主键ID
	
	private Long hid;//点击ID
	
	private Date createTime; //点击时间
	
	private String ip; // IP

	
}
