package com.korosmatick.sample.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.korosmatick.sample.model.db.PrototypeUser;

@Repository
@Transactional
public class PrototypeUserDaoImpl implements PrototypeUserDao{

	protected static Logger logger = Logger.getLogger("service");

	@Autowired
	private SessionFactory sessionFactory;
	
	/*@Autowired
    private PasswordEncoder passwordEncoder;*/
	
	public PrototypeUser findById(Long id) {
		logger.debug("Find User by id called");
		Session session = sessionFactory.getCurrentSession();
		PrototypeUser user = (PrototypeUser) session.get(PrototypeUser.class, id);
		return user;
	}

	public PrototypeUser findUserByEmail(String email) {
		logger.debug("Find User by email called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(PrototypeUser.class);
		criteria.add(Restrictions.eq("email", email));
		criteria.addOrder(Order.asc("id"));
		return criteria.list().isEmpty() ? null : (PrototypeUser) criteria.list().get(0);
	}

	@SuppressWarnings("unchecked")
	public List<PrototypeUser> findAllOrderedById() {
		logger.debug("Retrieving all users");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(PrototypeUser.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public Long add(PrototypeUser user) {
		logger.debug("Adding new User");
		Session session = sessionFactory.getCurrentSession();
		session.save(user);
		session.flush();
		return user.getId();
	}

	public void delete(Long id) {
		logger.debug("Deleting existing user");
		Session session = sessionFactory.getCurrentSession();
		PrototypeUser user = (PrototypeUser) session.get(PrototypeUser.class, id);
		session.delete(user);
	}

	public void setUserInitialized(PrototypeUser user) {
		// TODO Auto-generated method stub
		logger.debug("updating uesr");
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("UPDATE PrototypeUser SET initialized =:initialized WHERE id =:id")
				.setParameter("initialized", user.getInitialized())
				.setParameter("id", user.getId());
		@SuppressWarnings("unused")
		int result = query.executeUpdate();
	}

	public void updatePassword(PrototypeUser user) {
		// TODO Auto-generated method stub
		logger.debug("updating password");
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("UPDATE PrototypeUser SET encryptedPassword =:encryptedPassword, initialized =:initialized WHERE id =:id")
				.setParameter("encryptedPassword", user.getEncryptedPassword())
				.setParameter("initialized", 1)
				.setParameter("id", user.getId());
		@SuppressWarnings("unused")
		int result = query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<PrototypeUser> findAllUsersViewableToUser(PrototypeUser user) {
		// TODO Auto-generated method stub
		logger.debug("Retrieving all users Viewable to : " + user);
		Session session = sessionFactory.getCurrentSession();
		
		//TODO:
		return new ArrayList<PrototypeUser>();
		
	}

	@Override
	public void updateUser(PrototypeUser user) {
		logger.debug("Updating an existing user");
		Session session = sessionFactory.getCurrentSession();
		//PrototypeUser user = (PrototypeUser) session.get(PrototypeUser.class, id);
		session.update(user);
	}
}
