package com.dw.suppercms.infrastructure.web.security;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.security.UserRepository;

@Service
@Transactional
public class ShiroRealm
		extends AuthorizingRealm {

	@Autowired
	UserRepository userRepository;

	public ShiroRealm() {
		setName("shiroRealm");
		setCredentialsMatcher(new SimpleCredentialsMatcher());
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authtoken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authtoken;
		User user = userRepository.getByUsername(token.getUsername());
		if (user == null) {
			throw new UnknownAccountException();
		}
		user.getRole().getPermissions();
		return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.fromRealm(getName()).iterator().next();
		if (user != null) {
			user = userRepository.find(user.getId());
			SimpleAuthorizationInfo authos = new SimpleAuthorizationInfo();
			authos.addRole(user.getRole().getName());
			List<String> perms = user.getRole().getPermissions();
			authos.addStringPermissions(perms);
			return authos;
		} else {
			return null;
		}
	}
}
