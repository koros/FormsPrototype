package com.korosmatick.sample.service;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RapidproService {
	
	@Autowired 
	RequestHandler requestHandler;
	
	public static final String contactsEndPoint = "/api/v1/contacts.json";
	public static final String flowRunEndPoint = "/api/v1/runs.json";
	
	public JSONObject addContact(String uuid, String name, String language, JSONArray urns, List<String> group_uuids, Map<String,String> fields){
		return addOrUpdateContact(null, name, language, urns, group_uuids, fields);
	}
	
	public JSONObject updateContact(String uuid, String name, String language, JSONArray urns, List<String> group_uuids, Map<String,String> fields){
		return addOrUpdateContact(uuid, name, language, urns, group_uuids, fields);
	}
	
	/**
	 * @param uuid - the UUID of the contact to update (string) (optional, new contact created if not present)
	 * @param name - the full name of the contact (string, optional)
	 * @param language - the preferred language for the contact (3 letter iso code, optional)
	 * @param urns - a list of URNs you want associated with the contact (string array)
	 * @param group_uuids - a list of the UUIDs of any groups this contact is part of (string array, optional)
	 * @param fields - a JSON dictionary of contact fields you want to set or update on this contact (JSON, optional)
	 * @return
	 */
	public JSONObject addOrUpdateContact(String uuid, String name, String language, JSONArray urns, List<String> group_uuids, Map<String,String> fields){
		try {
			//null values are ignored automatically by put method
			JSONObject json = new JSONObject();
			json.put("uuid", uuid);
			json.put("name", name);
			json.put("language", language);
			json.put("urns", urns);
			json.put("group_uuids", group_uuids);
			json.put("fields", fields);
	        String data = json.toString();
	        
	        System.out.println(data);
	        
	        String responseStr = requestHandler.sendHttpRequest(contactsEndPoint, RequestHandler.POST, data);
	        if (responseStr != null) {
				return new JSONObject(responseStr);
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONObject listContacts(){
		try {
			String responseStr = requestHandler.sendHttpRequest(contactsEndPoint, RequestHandler.GET, null);
	        if (responseStr != null) {
				return new JSONObject(responseStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Deletes a specified contact
	 * see <a href="https://rapidpro.ona.io/api/v1/contacts">https://rapidpro.ona.io/api/v1/contacts</a>
	 * 
	 * @param contactUuid - uuid of the contact to be deleted
	 * @return true if the contact has been deleted false otherwise
	 */
	public boolean deleteContact(String contactUuid){
		try {
			return requestHandler.sendHttpDeleteRequest(contactsEndPoint, RequestHandler.DELETE, "?uuid=" + contactUuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param uuid - the UUID of the run (string) (filterable: uuid repeatable)
	 * @param flow_uuid - the UUID of the flow (string) (filterable: flow_uuid repeatable)
	 * @param contact - the UUID of the contact this run applies to (string) filterable: contact repeatable)
	 * @param group_uuids - the UUIDs of any groups this contact is part of (string array, optional) (filterable: group_uuids repeatable)
	 * @param created_on - the datetime when this run was started (datetime)
	 * @param modified_on - the datetime when this run was last modified (datetime) (filterable: before and after)
	 * @param completed - boolean indicating whether this run has completed the flow (boolean)
	 * @param expires_on - the datetime when this run will expire (datetime)
	 * @param expired_on - the datetime when this run expired or null if it has not yet expired (datetime or null)
	 * @param steps - steps visited by the contact on the flow (array of dictionaries)
	 * @param values - values collected during the flow run (array of dictionaries)
	 * @return
	 */
	public JSONObject listFlowRuns(String uuid, String flow_uuid, String contact, String group_uuids, String created_on, String modified_on, String completed, 
			String expires_on, String expired_on, JSONArray steps, JSONArray values){
		
		return null;
	}
	
	/**
	 * 
	 * @param flow_uuid - the UUID of the flow to start (string)
	 * @param contacts - the UUIDs of the contacts to start in the flow (string array, optional)
	 * @param groups - the UUIDs of any groups this contact is part of (string array, optional)
	 * @param extra - a set of extra variables. (dictionary)
	 * @param restart_participants - a boolean if True force restart of contacts already in a flow. (boolean, optional, defaults to True)
	 * @return
	 */
	public JSONArray startRunFlowForContact(String flow_uuid, List<String> contacts, List<String> groups, Map<String, String> extra, boolean restart_participants){
		try {
			//null values are ignored automatically by put method
			JSONObject json = new JSONObject();
			json.put("flow_uuid", flow_uuid);
			json.put("contacts", contacts);
			json.put("groups", groups);
			json.put("extra", extra);
			json.put("restart_participants", restart_participants);
	        String data = json.toString();
	        
	        System.out.println(data);
	        
	        String responseStr = requestHandler.sendHttpRequest(flowRunEndPoint, RequestHandler.POST, data);
	        if (responseStr != null) {
				return new JSONArray(responseStr);
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
