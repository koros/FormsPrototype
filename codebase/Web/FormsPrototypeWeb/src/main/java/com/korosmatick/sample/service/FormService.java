package com.korosmatick.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.korosmatick.sample.dao.FormDao;
import com.korosmatick.sample.model.db.Form;

@Service
public class FormService {

	@Autowired FormDao formDao;
	
	public List<Form> getAllForms(){
		return formDao.findAll();
	}
	
	public void saveForm(Form form){
		formDao.add(form);
	}
	
	public Form getFormById(Long id){
		return formDao.findById(id);
	}
}
