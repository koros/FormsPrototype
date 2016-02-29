package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.PrototypeUser;

public interface PrototypeUserDao {
	
	public PrototypeUser findById(Long id);
	
	public PrototypeUser findUserByEmail(String email);
	
	public List<PrototypeUser> findAllOrderedById();
	
	public Long add(PrototypeUser user);
	
	public void delete(Long id);
	
	public void setUserInitialized(PrototypeUser user);
	
	public void updatePassword(PrototypeUser user);
	
	public void updateUser(PrototypeUser user);
	
	public List<PrototypeUser> findAllUsersViewableToUser(PrototypeUser user);
	
}
