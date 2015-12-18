package com.korosmatick.sample.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.db.RequestsLogs;

@Controller
public class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	protected void logRequest(Map<String,String> allRequestParams){
		RequestsLogs logs = new RequestsLogs();
		logs.setAllRequestParams(allRequestParams.toString());
		logs.setTime(new Date().toString());
		requestsLogsDao.add(logs);
	}
	
	protected void logError(Exception e){
		//TODO:
		logger.error(e.toString());
	}
}
