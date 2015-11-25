package com.korosmatick.sample.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.korosmatick.sample.model.db.Form;

@Repository
@Transactional
public class FormDaoImpl implements FormDao{

	private static final Logger logger = LoggerFactory.getLogger(FormDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Form findById(Long id) {
		logger.debug("Find Form by id");
		Session session = sessionFactory.getCurrentSession();
		Form form = (Form) session.get(Form.class, id);
		return form;
	}

	@Override
	public Form findFormByNameAndId(String name, String formId) {
		logger.debug("Find Form by name : " + name );
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Form.class);
		criteria.add(Restrictions.eq("formName", name));
		criteria.add(Restrictions.eq("formId", formId));
		criteria.addOrder(Order.asc("id"));
		return criteria.list().isEmpty() ? null : (Form) criteria.list().get(0);
	}

	@Override
	public void add(Form form) {
		logger.debug("adding : " + form.getFormName());
		Session session = sessionFactory.getCurrentSession();
		session.save(form);
	}

	@Override
	public void delete(Form form) {
		logger.debug("Deleting existing Form");
		if (form.getId() != null) {
			deleteById(form.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		logger.debug("Deleting existing Form");
		Session session = sessionFactory.getCurrentSession();
		Form genericCategory = (Form) session.get(Form.class, id);
		session.delete(genericCategory);
	}

	@Override
	public List<Form> findAll() {
		logger.debug("findAll() called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Form.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
