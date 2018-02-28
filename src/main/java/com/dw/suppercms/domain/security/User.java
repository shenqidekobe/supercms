package com.dw.suppercms.domain.security;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * 
 * User
 *
 * @author osmos
 * @date 2015年9月17日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class User
		extends IdentifiedEntity {
	private static final long serialVersionUID = 356887475674106310L;

	@NotNull
	@Size(max = 32)
	private String username;
	@NotNull
	@Size(max = 32)
	private String password;
	@NotNull
	@Size(max = 32)
	private String name;
	@NotNull
	@Size(max = 32)
	private String email;
	@NotNull
	@Size(max = 11)
	private String telephone;
	@Size(max = 64)
	private String description;
	
	@NotNull
	private Long roleId;
	@ManyToOne
	@JoinColumn(name = "roleId", insertable = false, updatable = false)
	private Role role;

	public static User newOf(User user) {
		User newsUser = User.newOf();
		newsUser.setUsername(user.getUsername());
		newsUser.setPassword(user.getPassword());
		newsUser.setName(user.getName());
		newsUser.setEmail(user.getEmail());
		newsUser.setTelephone(user.getTelephone());
		newsUser.setDescription(user.getTelephone());
		newsUser.setRoleId(user.getRoleId());
		return newsUser;
	}

	public User alterOf(User user) {
		setUsername(user.getUsername());
		setPassword(user.getPassword());
		setName(user.getName());
		setEmail(user.getEmail());
		setTelephone(user.getTelephone());
		setDescription(user.getDescription());
		setRoleId(user.getRoleId());
		return this;
	}

}
