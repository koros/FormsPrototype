package com.korosmatick.sample.model.api;

public class Error extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String message;
	
	public Error(Throwable cause){
		super(cause);
	}
	
	public Error(String cause){
		super(cause);
	}
}
