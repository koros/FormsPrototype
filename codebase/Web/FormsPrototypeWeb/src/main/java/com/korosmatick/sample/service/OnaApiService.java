package com.korosmatick.sample.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.korosmatick.sample.dao.PrototypeUserDao;
import com.korosmatick.sample.model.api.Xform;
import com.korosmatick.sample.model.api.Xforms;
import com.korosmatick.sample.model.db.Form;
import com.korosmatick.sample.model.db.PrototypeUser;

@Service
public class OnaApiService {

	public static final String BASE_URL = "https://api.ona.io/";
	public static final String formsUrlEndPoint = "/formList";
	
	@Autowired
	PrototypeUserDao userDao;
	
	@Autowired
	HttpService httpService;
	
	@Autowired
	FormService formService;
	
	public void retrieveAndSaveAllFormsForUser(String username){
		try {
			PrototypeUser user = userDao.findUserByEmail(username);
		    String onaUserName  = user.getOnaAccountName();
		    String onaPassword = user.getOnaAccountPassword();
		    
		    if (onaUserName == null) {
				return;
			}
		    
			List<Xform> forms = getAllFormsBelongingToUser(username);
			if (forms != null) {
				for (Xform form : forms) {
					String formId = form.getFormID();
					//curl -X POST  -d "server_url=https://api.ona.io/<username>&form_id=date_survey" https://enketo.org/transform/get_html_form
					String urlString = "https://enketo.org/transform/get_html_form";
					String server_url = "https://api.ona.io/" + onaUserName;// "form_id=" + formId;
					
					Document doc = Jsoup.connect(urlString)
							  .data("server_url", server_url)
							  .data("form_id", formId)
							  // and other hidden fields which are being passed in post request.
							  .userAgent("Mozilla")
							  .post();
							   System.out.println(doc);
				    
				    String modelString = doc.select("model").toString();
				    if (modelString != null) {
				    	modelString = modelString.replaceAll("\n", "").replaceAll("\r", "").replaceAll(">\\s*<", "><");
				    	System.out.println(modelString);
					}
				    
				    
				    String formString = doc.select("form").toString();
				    System.out.println(formString);
				    
				    Form dbForm = new Form();
				    if (modelString != null && formString != null) {
				    	httpService.processXMLModel(modelString, dbForm);
				    	dbForm.setFormId(form.getFormID());
				    	dbForm.setFormName(form.getName());
				    	dbForm.setFormUrl(form.getDownloadUrl());
				    	dbForm.setFormVersion(form.getMajorMinorVersion());
				    	dbForm.setHash(form.getHash());
				    	dbForm.setModelNode(modelString);
				    	dbForm.setFormNode(formString);
						formService.saveForm(dbForm);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Xform> getAllFormsBelongingToUser(String username){
		try {
		    PrototypeUser user = userDao.findUserByEmail(username);
		    String onaUserName  = user.getOnaAccountName();
		    String onaPassword = user.getOnaAccountPassword();
		    
		    HttpRequestBase req = createHttpConnection(formsUrlEndPoint, RequestHandler.GET, null, onaUserName, onaPassword);
		    String response = executeRequest(req);
		    
		    InputStream stream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Xforms.class);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Xforms r = (Xforms) jaxbUnmarshaller.unmarshal(stream);
			
			List<Xform> forms = r.getXformList();
			for (Xform xform : forms) {
				System.out.println(xform.getName());
			}
			
			return forms;
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private HttpRequestBase createHttpConnection(String endPointUrl, int method, String data, final String username, final String password){
		try {
			HttpRequestBase req = null;
			switch (method) {
			case RequestHandler.POST:
				req = createHttpPostRequest(endPointUrl, data, username, password);
				break;
			case RequestHandler.GET:
				req = createHttpGetRequest(endPointUrl, data, username, password);
				break;
			default:
				break;
			}
			
			//req.setHeader("AUTHORIZATION", "Token " + AUTH_TOKEN);
			//req.setHeader("Content-Type", "application/json");
	        //post.setHeader("Accept", "application/json");
			req.setHeader("X-Stream" , "true");
			
//			Authenticator.setDefault (new Authenticator() {
//			    protected PasswordAuthentication getPasswordAuthentication() {
//			        return new PasswordAuthentication (username, password.toCharArray());
//			    }
//			});
			
	        return req;
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
	
	private HttpPost createHttpPostRequest(String endPointUrl, String data, String username, String password) throws UnsupportedEncodingException{
        HttpPost httpPost = new HttpPost(BASE_URL + username + endPointUrl);
        httpPost.setEntity(new StringEntity(data));
        return httpPost;
    }
	
    private HttpGet createHttpGetRequest(String endPointUrl, String params, String username, String password){
    	String urlString = BASE_URL + username + endPointUrl; 
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
