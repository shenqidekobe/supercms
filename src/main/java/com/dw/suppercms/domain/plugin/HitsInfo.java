package com.dw.suppercms.domain.plugin;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dw.suppercms.domain.manu.ManuscriptOrgan;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章点击量
 * */
@Entity
@Table(name="t_hits")
@Data
@EqualsAndHashCode(callSuper = false)
public class HitsInfo implements Serializable{

	private static final long serialVersionUID = -2585441332525226879L;
	
	@Id
	@GeneratedValue
	private Long hid;
	private Long siteId;  //站点ID
	private Long columnId;//栏目ID
	private Long recordId;//文章ID
	private String title; // 标题
	private String link; //文章地址
	private Integer count;// 点击量总数
	
	private Long manuId;//稿件ID
	
	@ManyToOne
	@JoinColumn(name = "organ_id")
	private ManuscriptOrgan organ;//投稿用户的单位用户ID
	
	@Transient
	private Integer ps;
	

}
