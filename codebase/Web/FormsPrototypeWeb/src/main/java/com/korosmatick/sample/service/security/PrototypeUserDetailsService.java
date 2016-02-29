package com.korosmatick.sample.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.db.PrototypeUser;


public class PrototypeUserDetailsService implements UserDetailsService{
	
	@Autowired
	PrototypeUserDao userDao;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if (username.isEmpty()) {
			throw new UsernameNotFoundException("Unable to locate a user with empty username: ");
		}
		
		PrototypeUser springUser = userDao.findUserByEmail(username);
		
		if (springUser != null) {
			return springUser;
		} else {
			throw new UsernameNotFoundException("Unable to locate a user with username: " + username);
		}
	}

}
