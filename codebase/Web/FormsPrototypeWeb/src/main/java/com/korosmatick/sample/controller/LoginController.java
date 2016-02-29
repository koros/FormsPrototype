package com.korosmatick.sample.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.db.PrototypeUser;

@Controller
public class LoginController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	PrototypeUserDao userDao;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SaltSource saltSource;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		createDevtestUser();
		
		return "login";
	}
	
	private void createDevtestUser() {
		String devtestEmail = "devtest1@gmail.com";
		String phone = "0720333333";
		String password = "devtest1";
		if (userDao.findUserByEmail(devtestEmail) == null) {
			PrototypeUser devtest = new PrototypeUser();
			devtest.setDeleted(0);
			devtest.setEnabled(1);
			devtest.setInitialized(1);
			devtest.setEmail(devtestEmail);
			
			String encodedPassword =  passwordEncoder.encodePassword(password, saltSource.getSalt(devtest));
			
            devtest.setEncryptedPassword(encodedPassword);
			
			devtest.setFirstName("Koros");
			devtest.setOtherNames("Vapnik");
			devtest.setPhoneNumber(phone);
			devtest.setRole("Root");
			userDao.add(devtest);
		}
		
	}
	
}
