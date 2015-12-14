package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.Contact;

public interface ContactDao {
	Contact findById(Long id);
	Contact findContactByNameAndUuid(String name, String uuid);
	void add(Contact contact);
	void delete(Contact contact);
	void deleteById(Long id);
	List<Contact> findAll();
}
