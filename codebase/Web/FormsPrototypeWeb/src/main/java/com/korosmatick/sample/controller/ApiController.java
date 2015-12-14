package com.korosmatick.sample.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.api.Response;
import com.korosmatick.sample.model.api.ResponseWrapper;
import com.korosmatick.sample.model.db.RequestsLogs;
import com.korosmatick.sample.service.FormService;

@Controller
public class ApiController {
	
	@Autowired FormService formService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@RequestMapping(value="/rest/forms")
	public @ResponseBody ResponseWrapper getForms() {
		
		ResponseWrapper apiResponse = new ResponseWrapper();
		Response response = new Response();
		try {
			
			response.setStatus("ok");
			response.setForms(formService.getAllForms());
			
		} catch (Exception e) {
			response.setStatus("error");
			response.setError(e.toString());
		}
		
		apiResponse.setResponse(response);
		
		return apiResponse;
	}
	
	@RequestMapping(value="/rest/rapidpro")
	public @ResponseBody Map<String, Object> rapidProHook(@RequestParam Map<String,String> allRequestParams, Locale locale) {
		
		RequestsLogs logs = new RequestsLogs();
		logs.setAllRequestParams(allRequestParams.toString());
		logs.setTime(new Date().toString());
		requestsLogsDao.add(logs);
		
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value="/rest/rapidpro/mother")
	public @ResponseBody Map<String, Object> rapidProHookMotherId(@RequestParam Map<String,String> allRequestParams, Locale locale, @RequestParam(value = "motherId") Long motherId) {
		
		Map<String, Object> dummyResponse= new HashMap<String, Object>();
		
		RequestsLogs logs = new RequestsLogs();
		logs.setAllRequestParams(allRequestParams.toString());
		logs.setTime(new Date().toString());
		requestsLogsDao.add(logs);
		
		Long serverSideDummyId = null;
		if (motherId > 0 && motherId < 10 ) {
			serverSideDummyId = Long.valueOf(motherId.toString().concat(motherId.toString()).concat(motherId.toString().concat(motherId.toString())));
		}
		
		if (serverSideDummyId != null) {
			dummyResponse.put("motherId", serverSideDummyId);
			dummyResponse.put("motherName", "Mother 000" + motherId);
		}
		
		return dummyResponse;
	}
	
	@RequestMapping(value="/rest/rapidpro/clinic")
	public @ResponseBody Map<String, Object> rapidProHookClinic(@RequestParam Map<String,String> allRequestParams, Locale locale, @RequestParam(value = "clinicName") String clinicName) {
		
		Map<String, Object> dummyResponse= new HashMap<String, Object>();
		
		RequestsLogs logs = new RequestsLogs();
		logs.setAllRequestParams(allRequestParams.toString());
		logs.setTime(new Date().toString());
		requestsLogsDao.add(logs);
		
		dummyResponse.put("clinicName", clinicName);
		
		return dummyResponse;
	}
	
}
