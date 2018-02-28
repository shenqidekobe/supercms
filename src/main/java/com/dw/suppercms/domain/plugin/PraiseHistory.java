package com.dw.suppercms.domain.plugin;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞历史
 * @kobe
 * */
@Entity
@Table(name="t_praise_history")
@Data
@EqualsAndHashCode(callSuper = false)
public class PraiseHistory implements Serializable{
	
	private static final long serialVersionUID = -7279561956749934950L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;//主键ID
	
	private Long pid;//点赞ID
	
	private Date createTime; // 点赞日期
	
	private String ip; // IP

}
