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

import com.korosmatick.sample.model.db.RequestsLogs;

@Repository
@Transactional
public class RequestsLogsDaoImpl implements RequestsLogsDao{

	private static final Logger logger = LoggerFactory.getLogger(RequestsLogsDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public RequestsLogs findById(Long id) {
		logger.debug("Find Form by id");
		Session session = sessionFactory.getCurrentSession();
		RequestsLogs form = (RequestsLogs) session.get(RequestsLogs.class, id);
		return form;
	}

	@Override
	public void add(RequestsLogs requestsLogs) {
		logger.debug("adding : " + requestsLogs);
		Session session = sessionFactory.getCurrentSession();
		session.save(requestsLogs);
	}

	@Override
	public void delete(RequestsLogs requestsLogs) {
		logger.debug("Deleting existing Form");
		if (requestsLogs.getId() != null) {
			deleteById(requestsLogs.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		logger.debug("Deleting existing Form");
		Session session = sessionFactory.getCurrentSession();
		RequestsLogs genericCategory = (RequestsLogs) session.get(RequestsLogs.class, id);
		session.delete(genericCategory);
	}

	@Override
	public List<RequestsLogs> findAll() {
		logger.debug("findAll() called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RequestsLogs.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
