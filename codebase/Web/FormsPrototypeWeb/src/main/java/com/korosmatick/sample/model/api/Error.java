package com.korosmatick.sample.model.api;

public class Error extends Exception{

	String message;
	
	public Error(Throwable cause){
		super(cause);
	}
	
	public Error(String cause){
		super(cause);
	}
}
