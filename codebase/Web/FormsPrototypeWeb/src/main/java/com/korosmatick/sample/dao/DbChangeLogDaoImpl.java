package com.korosmatick.sample.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.korosmatick.sample.model.db.DbChangeLog;

@Repository
@Transactional
public class DbChangeLogDaoImpl implements DbChangeLogDao{

	private static final Logger logger = LoggerFactory.getLogger(DbChangeLogDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public DbChangeLog findById(Long id) {
		logger.debug("Find DbChangeLog by id");
		Session session = sessionFactory.getCurrentSession();
		DbChangeLog dbChangeLog = (DbChangeLog) session.get(DbChangeLog.class, id);
		return dbChangeLog;
	}

	@Override
	public void add(DbChangeLog dbChangeLog) {
		logger.debug("adding changelog for table: " + dbChangeLog.getTableName());
		Session session = sessionFactory.getCurrentSession();
		session.save(dbChangeLog);
	}

	@Override
	public void delete(DbChangeLog dbChangeLog) {
		logger.debug("Deleting existing dbChangeLog");
		if (dbChangeLog.getId() != null) {
			deleteById(dbChangeLog.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		logger.debug("Deleting existing dbChangeLog");
		Session session = sessionFactory.getCurrentSession();
		DbChangeLog contact = (DbChangeLog) session.get(DbChangeLog.class, id);
		session.delete(contact);
	}

	@Override
	public List<DbChangeLog> findAll() {
		logger.debug("findAll() called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(DbChangeLog.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
