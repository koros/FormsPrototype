package com.korosmatick.sample.controller.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.korosmatick.sample.controller.BaseController;
import com.korosmatick.sample.dao.RequestsLogsDao;
import com.korosmatick.sample.model.api.Response;
import com.korosmatick.sample.model.api.ResponseWrapper;
import com.korosmatick.sample.model.api.SyncRequestPacket;
import com.korosmatick.sample.model.api.SyncResponse;
import com.korosmatick.sample.service.FormService;
import com.korosmatick.sample.service.SyncManager;
import com.korosmatick.sample.util.ResponseUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Controller
public class SyncController extends BaseController{
	
	@Autowired FormService formService;
	
	@Autowired
	RequestsLogsDao requestsLogsDao;
	
	@Autowired
	private ComboPooledDataSource dataSource;
	
	
	@RequestMapping(value="/rest/sync",method = RequestMethod.POST)
	public @ResponseBody ResponseWrapper syncData(@RequestParam Map<String,String> allRequestParams, @RequestParam String payload) {
		ResponseWrapper apiResponse = new ResponseWrapper();
		try {
			Response response = new Response();
			response.setStatus("ok");
			logRequest(allRequestParams);
			
			Gson gson = new Gson();
			SyncRequestPacket syncRequestPacket = gson.fromJson(payload, SyncRequestPacket.class);
			
			SyncManager syncManager = new SyncManager(dataSource.getConnection());
			SyncResponse syncResponse = syncManager.handleSyncRequest(syncRequestPacket);
			
			response.setSyncResponse(syncResponse);
			
			apiResponse.setResponse(response);
			return apiResponse;
			
		} catch (Exception e) {
			e.printStackTrace();
			Response response = new Response();
			response.setStatus("error");
			ResponseUtils.addErrorToResponse(e, response);
			apiResponse.setResponse(response);
			return apiResponse;
		}
		
	}
	
}
