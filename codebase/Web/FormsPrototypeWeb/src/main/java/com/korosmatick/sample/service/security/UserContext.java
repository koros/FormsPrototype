package com.korosmatick.sample.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.db.PrototypeUser;

@Component
public class UserContext {
	
	@Autowired
	PrototypeUserDao userDao;
	
	public long currentUserId(){
		String username = currentlyLoggedInUserName();
		if (username != null) {
			PrototypeUser user = userDao.findUserByEmail(username);
			return user.getId();
		}
		return 0l;
	}
	
	public String currentlyLoggedInUserName(){
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication == null) {
			return null;
		}
		return authentication.getName();
	}
	
	public String currentlyLoggedInUserDispayName(){
		String username = currentlyLoggedInUserName();
		if (username != null) {
			PrototypeUser user = userDao.findUserByEmail(username);
			return user.getFirstName();
		}
		return "";
	}
	
	public String currentlyLoggedInUserFullName(){
		String username = currentlyLoggedInUserName();
		if (username != null) {
			PrototypeUser user = userDao.findUserByEmail(username);
			return user.getFirstName() +" " + user.getOtherNames();
		}
		return "";
	}
	
	public PrototypeUser currentlyLoggedInUser(){
		String username = currentlyLoggedInUserName();
		if (username != null) {
			return userDao.findUserByEmail(username);
		}
		return null;
	}

	public PrototypeUser getUserById(Long id) {
		// TODO Auto-generated method stub
		return userDao.findById(id);
	}
	
}
