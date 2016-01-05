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

import com.korosmatick.sample.model.db.Contact;

@Repository
@Transactional
public class ContactDaoImpl implements ContactDao{

	private static final Logger logger = LoggerFactory.getLogger(FormDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Contact findById(Long id) {
		logger.debug("Find Contact by id");
		Session session = sessionFactory.getCurrentSession();
		Contact contact = (Contact) session.get(Contact.class, id);
		return contact;
	}

	@Override
	public Contact findContactByNameAndUuid(String name, String uuid) {
		logger.debug("Find Contact by name : " + name );
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Contact.class);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("uuid", uuid));
		criteria.addOrder(Order.asc("id"));
		return criteria.list().isEmpty() ? null : (Contact) criteria.list().get(0);
	}

	@Override
	public void add(Contact contact) {
		logger.debug("adding : " + contact.getName());
		Session session = sessionFactory.getCurrentSession();
		session.save(contact);
	}

	@Override
	public void delete(Contact contact) {
		logger.debug("Deleting existing Contact");
		if (contact.getId() != null) {
			deleteById(contact.getId());
		}
	}

	@Override
	public void deleteById(Long id) {
		logger.debug("Deleting existing Contact");
		Session session = sessionFactory.getCurrentSession();
		Contact contact = (Contact) session.get(Contact.class, id);
		session.delete(contact);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Contact> findAll() {
		logger.debug("findAll() called");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Contact.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
