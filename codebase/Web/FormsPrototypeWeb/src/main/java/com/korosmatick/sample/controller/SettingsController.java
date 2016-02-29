package com.korosmatick.sample.controller;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingsController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
	
	@RequestMapping(value="/prototype/settings")
	public String settings(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		logRequest(allRequestParams);
		return "settings-tiles";
	}
	
}
