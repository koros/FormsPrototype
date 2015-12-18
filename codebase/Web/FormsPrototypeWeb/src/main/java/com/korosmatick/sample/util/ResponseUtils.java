package com.korosmatick.sample.util;

import java.util.ArrayList;
import java.util.List;

import com.korosmatick.sample.model.api.Error;
import com.korosmatick.sample.model.api.Response;

public class ResponseUtils {

	public static Response addErrorToResponse(Exception e, Response response){
		List<Error> errors = response.getErrors();
		if (errors == null) {
			errors =  new ArrayList<Error>();
		}
		
		Error error = new Error(e);
		errors.add(error);
		response.setErrors(errors);
		return response;
	}
}
