package com.dw.suppercms.domain.data;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * Tag
 *
 * @author osmos
 * @date 2015年9月8日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Tag
		extends IdentifiedEntity {
	private static final long serialVersionUID = -5541406234486958125L;

	private String title;

	/**
	 * build a new tag
	 * 
	 * @param tag holds the new building tag state
	 * @return the built model
	 */
	public static Tag newOf(Tag tag) {
		Tag newTag = Tag.newOf();
		newTag.setTitle(tag.getTitle());
		return newTag;
	}

	/**
	 * alter tag state
	 * 
	 * @param tag holds the altering tag state
	 * @return the altered tag
	 */
	public Tag alterOf(Tag tag) {
		setTitle(tag.getTitle());
		return this;
	}
}
