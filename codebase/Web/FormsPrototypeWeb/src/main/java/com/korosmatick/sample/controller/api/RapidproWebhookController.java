package com.korosmatick.sample.controller.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korosmatick.sample.controller.BaseController;
import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.service.FormService;

@Controller
public class RapidproWebhookController extends BaseController{

	@Autowired FormService formService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@RequestMapping(value="/rest/rapidpro")
	public @ResponseBody Map<String, Object> rapidProHook(@RequestParam Map<String,String> allRequestParams, Locale locale) {
		logRequest(allRequestParams);
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value="/rest/rapidpro/mother")
	public @ResponseBody Map<String, Object> rapidProHookMotherId(@RequestParam Map<String,String> allRequestParams, Locale locale, @RequestParam(value = "motherId") Long motherId) {
		
		Map<String, Object> dummyResponse= new HashMap<String, Object>();
		logRequest(allRequestParams);
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
		logRequest(allRequestParams);
		dummyResponse.put("clinicName", clinicName);
		
		return dummyResponse;
	}
}
