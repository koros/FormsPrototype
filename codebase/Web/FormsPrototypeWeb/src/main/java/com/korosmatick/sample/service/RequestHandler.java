package com.korosmatick.sample.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class RequestHandler {

	private static final String AUTH_TOKEN = "";
	private static final String BASE_URL = "http://rapidpro.ona.io";
	
	public static final int POST = 1;
	public static final int GET = 2;
	public static final int DELETE = 3;
	
	public String sendHttpRequest(String endPointUrl, int method, String data){
        HttpRequestBase httpReq = createHttpConnection(endPointUrl , method, data);
        return executeRequest(httpReq);
    }
	
	public boolean sendHttpDeleteRequest(String endPointUrl, int method, String data){
		HttpRequestBase httpReq = createHttpConnection(endPointUrl , method, data);
        return executeRequest(httpReq) != null;
	}
	
	private HttpRequestBase createHttpConnection(String endPointUrl, int method, String data){
		try {
			HttpRequestBase post = null;
			switch (method) {
			case POST:
				post = createHttpPostRequest(endPointUrl, data);
				break;
			case GET:
				post = createHttpGetRequest(endPointUrl, data);
				break;
			case DELETE:
				post = createHttpDeleteRequest(endPointUrl, data);
				break;
			default:
				break;
			}
			
	        post.setHeader("AUTHORIZATION", "Token " + AUTH_TOKEN);
	        post.setHeader("Content-Type", "application/json");
	        post.setHeader("Accept", "application/json");
	        post.setHeader("X-Stream" , "true");
	        return post;
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
	
	private HttpPost createHttpPostRequest(String endPointUrl, String data) throws UnsupportedEncodingException{
        HttpPost httpPost = new HttpPost(BASE_URL + endPointUrl);
        httpPost.setEntity(new StringEntity(data));
        return httpPost;
    }
	
	private HttpDelete createHttpDeleteRequest(String endPointUrl, String params) throws UnsupportedEncodingException{
		String urlString = BASE_URL + endPointUrl; 
    	urlString = params != null ? urlString + params : urlString;
        HttpDelete httpDelete = new HttpDelete(urlString);
        return httpDelete;
    }
	
    private HttpGet createHttpGetRequest(String endPointUrl, String params){
    	String urlString = BASE_URL + endPointUrl; 
    	urlString = params != null ? urlString + params : urlString;
        HttpGet httpGet = new HttpGet(urlString);
        return httpGet;
    }
	
    private String executeRequest(HttpRequestBase httpReq){
        try{
        	HttpResponse response=null;
            String line = "";
            StringBuffer result = new StringBuffer();
            HttpClient client = HttpClientBuilder.create().build();
            response = client.execute(httpReq);
            System.out.println("Response Code : " +response.getStatusLine().getStatusCode());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while ((line = reader.readLine()) != null){
            	result.append(line); 
            }
            String apiResponseString = result.toString();
            System.out.println(apiResponseString);
            return apiResponseString;
        }
        catch (UnsupportedEncodingException e){
            System.out.println("error while encoding api url : "+e);
        }
        catch (IOException e){
            System.out.println("ioException occured while sending http request : "+e);
        }
        catch(Exception e){
            System.out.println("exception occured while sending http request : "+e);
        }
        finally{
        	httpReq.releaseConnection();
        }
        return null;
    }
    
    
    
}
