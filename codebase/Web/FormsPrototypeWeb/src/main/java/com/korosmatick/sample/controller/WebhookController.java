package com.korosmatick.sample.controller;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.db.RequestsLogs;
import com.korosmatick.sample.service.FormService;
import com.korosmatick.sample.service.HttpService;

@Controller
public class WebhookController {

	private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
	
	@Autowired
	FormService formService;
	
	@Autowired
	HttpService httpService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@RequestMapping(value = "/prototype/webhook", method = RequestMethod.GET)
	public String testWebhookUrl(Locale locale, Model model, @RequestParam Map<String,String> allRequestParams) {
		logger.info("Welcome Admin home! The client locale is {}.", locale);
		
		RequestsLogs logs = new RequestsLogs();
		logs.setAllRequestParams(allRequestParams.toString());
		logs.setTime(new Date().toString());
		//requestsLogsDao.add(logs);
		
		model.addAttribute("requestsLogs", requestsLogsDao.findAll());
		return "webhook-tiles";
	}
}
