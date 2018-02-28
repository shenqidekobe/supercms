package com.dw.suppercms.domain.security;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 
 * Role
 *
 * @author osmos
 * @date 2015年9月17日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Role
		extends IdentifiedEntity {
	private static final long serialVersionUID = -6702603906740595047L;

	@NotNull
	@Size(max = 32)
	private String name;
	@Size(max = 64)
	private String description;
	
	@ElementCollection
	@CollectionTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission")
	private List<String> permissions = new ArrayList<String>();

	

	public static Role newOf(Role role) {
		Role newRole = Role.newOf();
		newRole.setName(role.getName());
		newRole.setDescription(role.getDescription());
		return newRole;
	}

	public Role alterOf(Role role) {
		setName(role.getName());
		setDescription(role.getDescription());
		return this;
	}
}
