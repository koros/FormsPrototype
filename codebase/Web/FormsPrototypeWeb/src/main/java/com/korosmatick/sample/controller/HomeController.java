package com.korosmatick.sample.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	com.korosmatick.sample.dao.FormDao formDao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		//create sample form 
//		Form form = new Form();
//		form.setFormId("123124");
//		form.setFormName("sampel form");
//		form.setFormUrl("http://sampleurl.com");
//		formDao.add(form);
		
		return "adminHome-tiles";
	}
	
	@RequestMapping(value = "/sample")
	public void handle(@RequestBody String body, @RequestParam Map<String,String> allRequestParams) throws IOException {
		logger.info("Received request ", body);
	}
	
	@RequestMapping(value = "/prototype/webhook")
	public void testRapidproWebhookUrl(@RequestBody String body, @RequestParam Map<String,String> allRequestParams) throws IOException {
		logger.info("Received request ", body);
	}
}

