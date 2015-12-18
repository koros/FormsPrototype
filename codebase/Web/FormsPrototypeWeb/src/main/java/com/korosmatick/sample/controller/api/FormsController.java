package com.korosmatick.sample.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korosmatick.sample.controller.BaseController;
import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.api.Response;
import com.korosmatick.sample.model.api.ResponseWrapper;
import com.korosmatick.sample.service.FormService;
import com.korosmatick.sample.util.ResponseUtils;

@Controller
public class FormsController extends BaseController{
	
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
			e.printStackTrace();
			ResponseUtils.addErrorToResponse(e, response);
		}
		
		apiResponse.setResponse(response);
		
		return apiResponse;
	}
	
	
	
}
