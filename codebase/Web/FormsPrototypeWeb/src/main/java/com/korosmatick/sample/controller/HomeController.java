package com.korosmatick.sample.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.db.PrototypeUser;
import com.korosmatick.sample.service.OnaApiService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	com.korosmatick.sample.dao.FormDao formDao;
	
	@Autowired
	OnaApiService onaApiService;
	
	@Autowired
	PrototypeUserDao userDao;
	
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
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
	    PrototypeUser currentUser = userDao.findUserByEmail(name);
	    
		onaApiService.retrieveAndSaveAllFormsForUser(currentUser.getEmail());
		
		return "redirect:" + "/prototype/forms";
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

