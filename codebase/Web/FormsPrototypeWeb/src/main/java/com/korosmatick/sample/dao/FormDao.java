package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.Form;

public interface FormDao {
	Form findById(Long id);
	Form findFormByNameAndId(String name, String formId);
	void add(Form form);
	void delete(Form form);
	void deleteById(Long id);
	List<Form> findAll();
}
