package com.dw.suppercms.application.security.impl;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.SpringHelper;
import com.dw.framework.core.annotation.ApplicationService;
import com.dw.framework.core.exception.BusinessException;
import com.dw.suppercms.application.security.UserService;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.security.UserRepository;
import com.googlecode.genericdao.search.SearchResult;

/**
 * 
 * UserServiceImpl
 *
 * @author osmos
 * @date 2015年9月17日
 */
@ApplicationService
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Boolean validateUsername(String username) {
		return userRepository.countByUsername(username) == 0 ? true : false;
	}

	@Override
	public User create(User user) {
		User newUser = User.newOf(user);
		userRepository.save(newUser);
		File userDir = new File(SpringHelper.servletContext.getRealPath("/") + Module.USER_DIR + "/" + user.getUsername());
		userDir.mkdirs();
		return newUser;

	}

	@Override
	public User retrieve(Long id) {

		assertNotNull(id);

		User user = userRepository.find(id);
		boolean success = (user != null ? true : false);

		if (success) {
			return user;
		} else {
			throw new BusinessException(String.format("用户在数据库中不存在：id=%s", id));
		}
	}

	@Override
	public User update(Long id, User newUser) {

		assertNotNull(id);

		User dbUser = retrieve(id);
		
		if(!newUser.getUsername().equals(dbUser.getUsername())){
			File oldDir = new File(SpringHelper.servletContext.getRealPath("/") + Module.USER_DIR + "/" + dbUser.getUsername());
			if(!oldDir.exists()){
				oldDir.mkdirs();
			}
			oldDir.renameTo(new File(SpringHelper.servletContext.getRealPath("/") + Module.USER_DIR + "/" + newUser.getUsername()));
		}
		
		userRepository.save(dbUser.alterOf(newUser));
		
		

		return dbUser;
	}

	@Override
	public Long delete(Long id) {

		assertNotNull(id);

		User oldUser = retrieve(id);
		userRepository.remove(oldUser);

		return id;
	}

	@Override
	public SearchResult<User> paginateAll(Map<String, Object> condition) {
		return userRepository.paginateAll(condition);
	}
	
	@Override
	public List<User> all(){
		return this.userRepository.findAll();
	}

	@Override
	public User retriveByUsername(String username) {
		return userRepository.getByUsername(username);
	}
}
