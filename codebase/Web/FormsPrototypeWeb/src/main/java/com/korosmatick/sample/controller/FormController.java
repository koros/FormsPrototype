package com.korosmatick.sample.controller;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.db.Form;
import com.korosmatick.sample.service.FormService;
import com.korosmatick.sample.service.HttpService;

@Controller
public class FormController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(FormController.class);
	
	@Autowired
	FormService formService;
	
	@Autowired
	HttpService httpService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@RequestMapping(value = "/prototype/forms", method = RequestMethod.GET)
	public String forms(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		logRequest(allRequestParams);
		model.addAttribute("forms", formService.getAllForms());
		return "formsList-tiles";
	}
	
	@RequestMapping(value = "/prototype/addForm")
	public String addForm(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		logRequest(allRequestParams);
		model.addAttribute("form", new Form());
		return "addForm-tiles";
	}
	
	@RequestMapping(value = "/prototype/linkForms")
	public String linkForms(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		logRequest(allRequestParams);
		model.addAttribute("forms", formService.getAllForms());
		return "linkForms-tiles";
	}
	
	@RequestMapping(value = "/prototype/addNewFormAction")
	public String addNewFormAction(Locale locale, Model model, @ModelAttribute Form form, @RequestParam Map<String,String> allRequestParams) {
		logger.info("addNewFormAction");
		logRequest(allRequestParams);
		String formUrl = form.getFormUrl();
		String formName = form.getFormName();
		
		logger.info("formUrl", "URL :" + formUrl);
		logger.info("name", "NAME : " + formName);
		
		if(httpService.fetchForm(form)){
			model.addAttribute("formSaved", true);
		}else{
			model.addAttribute("formSaved", false);
		}
		
		model.addAttribute("forms", formService.getAllForms());
		return "addForm-success-tiles";
	}
	
	@RequestMapping(value = "/editForm/{id}", method = RequestMethod.GET)
	public String editNewsSource(@PathVariable("id") String id, Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("============== editForm ==============");
        try{
        	Form form = formService.getFormById(Long.valueOf(id));
        	logRequest(allRequestParams);
        	model.addAttribute("form", form);
        	return "editForm-tiles";
        }
        catch(Exception e){
            return null;
        }
	}
}
