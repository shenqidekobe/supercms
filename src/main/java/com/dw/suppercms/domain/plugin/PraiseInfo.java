package com.dw.suppercms.domain.plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dw.suppercms.domain.manu.ManuscriptOrgan;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞实体
 * 
 * @author osmos
 * 
 */
@Entity
@Table(name="t_praise")
@Data
@EqualsAndHashCode(callSuper = false)
public class PraiseInfo implements java.io.Serializable {
	
	private static final long serialVersionUID = 9004320132060905524L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long pid;// 数据标识
	private Long recordId;//文章ID
	private Long columnId;//栏目ID
	private Long siteId;//站点ID
	private String title; // 标题
	private String link; //文章地址
	private Integer count;// 已获点赞总数
	
	private Long manuId;//稿件ID
	
	@ManyToOne
	@JoinColumn(name = "organ_id")
	private ManuscriptOrgan organ;
	
	private Integer virtualCount;//虚拟点赞数量,可手动设置
	
	@Transient
	private Integer ps;
	

}
