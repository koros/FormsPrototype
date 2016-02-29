package com.korosmatick.sample.controller;

import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.db.Contact;
import com.korosmatick.sample.model.db.PrototypeUser;

@Controller
public class OnaAccountController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	PrototypeUserDao userDao;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SaltSource saltSource;

	@RequestMapping(value = "/prototype/viewOnaAccount", method = RequestMethod.GET)
	public String viewOnaAccount(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
	    PrototypeUser user = userDao.findUserByEmail(name);
	    model.addAttribute("user", user);
		
		return "viewOnaAccount-tiles";
	}
	
	@RequestMapping(value = "/prototype/editOnaAccountAction", method = RequestMethod.POST)
	public String rapidproAddContactAction(Locale locale, Model model, @ModelAttribute PrototypeUser user, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		
		logRequest(allRequestParams);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
	    PrototypeUser currentUser = userDao.findUserByEmail(name);
	    
	    currentUser.setOnaAccountName(user.getOnaAccountName());
	    currentUser.setOnaAccountPassword(user.getOnaAccountPassword());
	    
	    userDao.updateUser(currentUser);
		
		return "redirect:" + "/prototype/forms";
	}
	
}
