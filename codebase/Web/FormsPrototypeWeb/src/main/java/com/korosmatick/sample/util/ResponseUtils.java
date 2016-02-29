package com.korosmatick.sample.util;

import java.util.ArrayList;
import java.util.List;

import com.korosmatick.sample.model.api.Error;
import com.korosmatick.sample.model.api.Response;

public class ResponseUtils {

	/**
	 * Appends error messages to the response object error list
	 * @param e
	 * @param response
	 * @return
	 */
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
