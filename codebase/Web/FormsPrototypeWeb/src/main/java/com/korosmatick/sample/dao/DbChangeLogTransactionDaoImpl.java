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

import com.korosmatick.sample.model.db.DbChangeLogTransaction;

@Repository
@Transactional
public class DbChangeLogTransactionDaoImpl implements DbChangeLogTransactionDao{

private static final Logger logger = LoggerFactory.getLogger(DbChangeLogDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public DbChangeLogTransaction findById(Long id) {
		logger.debug("Find DbChangeLogTransaction by id");
		Session session = sessionFactory.getCurrentSession();
		DbChangeLogTransaction dbChangeLogTransaction = (DbChangeLogTransaction) session.get(DbChangeLogTransaction.class, id);
		return dbChangeLogTransaction;
	}

	@Override
	public void add(DbChangeLogTransaction dbChangeLogTransaction) {
		logger.debug("adding DbChangeLogTransaction");
		Session session = sessionFactory.getCurrentSession();
		session.save(dbChangeLogTransaction);
	}

	@Override
	public void delete(DbChangeLogTransaction dbChangeLogTransaction) {
		logger.debug("Deleting existing DbChangeLogTransaction");
		if (dbChangeLogTransaction.getId() != null) {
			deleteById(dbChangeLogTransaction.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		logger.debug("Deleting existing dbChangeLogTransaction");
		Session session = sessionFactory.getCurrentSession();
		DbChangeLogTransaction contact = (DbChangeLogTransaction) session.get(DbChangeLogTransaction.class, id);
		session.delete(contact);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DbChangeLogTransaction> findAll() {
		logger.debug("findAll() called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(DbChangeLogTransaction.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
