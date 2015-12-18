package com.korosmatick.sample.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
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

import com.korosmatick.sample.dao.ContactDao;
import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.db.Contact;
import com.korosmatick.sample.service.RapidproService;

@Controller
public class RapidproContactsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(RapidproContactsController.class);
	
	@Autowired
	RapidproService rapidproService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@Autowired
	ContactDao contactDao;
	
	@RequestMapping(value = "/prototype/rapidpro/contacts")
	public String rapidproContacts(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		
		logRequest(allRequestParams);
		
		model.addAttribute("contacts", contactDao.findAll());
		return "rapidproContacts-tiles";
	}
	
	@RequestMapping(value = "/prototype/rapidpro/addContact")
	public String rapidproAddContact(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		
		logRequest(allRequestParams);
		
		model.addAttribute("contact", new Contact());
		return "rapidproAddContact-tiles";
	}
	
	@RequestMapping(value = "/prototype/rapidpro/addNewContactAction", method = RequestMethod.POST)
	public String rapidproAddContactAction(Locale locale, Model model, @ModelAttribute Contact contact, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		
		logRequest(allRequestParams);
		
		JSONArray urns = new JSONArray();
		String tel = "tel:".concat(contact.getPhone());
		urns.put(tel);
		
		JSONObject rapidproContact  = rapidproService.addContact(null, contact.getName(), null, urns, null, null);
		contact.setUuid(rapidproContact.getString("uuid"));
		contactDao.add(contact);
		
		model.addAttribute("contacts", contactDao.findAll());
		return "redirect:" + "/prototype/rapidpro/contacts";
	}
	
	@RequestMapping(value = "/prototype/rapidpro/runSampleAncFlow/{id}")
	public String runSampleAncFlow(@PathVariable("id") String id, Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("============== runSampleAncFlow ==============");
        try{
        	Contact contact = contactDao.findById(Long.valueOf(id));
        	
        	logRequest(allRequestParams);
    		
    		String flow_uuid = "8adbace9-1824-4a17-880c-a97629d2b2ae"; // sample ANC flow hard coded for now
			List<String> contacts = new ArrayList<String>();
			contacts.add(contact.getUuid());
			JSONArray status = rapidproService.startRunFlowForContact(flow_uuid, contacts , null, null, true);
			
        	model.addAttribute("status", status);
        	return "startFlowStatus-tiles";
        }
        catch(Exception e){
            return null;
        }
	}
	
}
