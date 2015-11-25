package com.korosmatick.sample.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.korosmatick.sample.model.db.Form;

@Service
public class HttpService {

	@Autowired
	FormService formService;

	@Autowired
	private DataSource dataSource;

	// https://sujen.enketo.org/webform http://sujen.enketo.org/webform
	public boolean fetchForm(Form form) {
		try {
			String url = form.getFormUrl();
			disableSSLCertCheck();
			Document doc = Jsoup.connect(url).userAgent("Safari").timeout(0).get();

			// get the model String
			String modelString = extractModelStringFromDoc(doc);
			
			// get the Form tag
			String formString = extractFormTagFromDoc(doc);

			if (modelString != null && formString != null) {
				processXMLModel(modelString, form);
				form.setModelNode(modelString);
				form.setFormNode(formString);
				formService.saveForm(form);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	private String extractFormTagFromDoc(Document doc){
		String formString = null;
		if (doc != null) {
			Elements els = doc.getElementsByTag("form");
			if (els != null) {
				for (Element el : els) {
					if (el.outerHtml().contains("This form was created by transforming a OpenRosa-flavored")) {
						formString = el.outerHtml();
						System.out.println(formString);
					}
				}
			}
		}
		return formString;
	}
	
	private String extractModelStringFromDoc(Document doc){
		String modelString = null;
		if (doc != null) {
			Elements els = doc.select("script");
			if (els != null) {
				for (Element el : els) {
					if (el.html().contains("var settings = {};")) {

						String htmlScript = el.html();
						System.out.println(htmlScript);

						String pattern = "(var modelStr = \")(.*)(\")";
						// Create a Pattern object
						Pattern r = Pattern.compile(pattern);

						// Now create matcher object.
						Matcher m = r.matcher(htmlScript);
						if (m.find()) {
							System.out.println("Found value: " + m.group(0));
							System.out.println("Found value: " + m.group(1));
							modelString = m.group(2);
							System.out.println("Found value: " + modelString);
						} else {
							System.out.println("NO MATCH");
						}
					}
				}
			}
		}
		return modelString;
	}
	
	public Form processXMLModel(String xmlModelString, Form form) {
		try {
			xmlModelString = xmlModelString.replaceAll("\\\\/", "/").replaceAll("\\\\\"", "\"");
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(xmlModelString.getBytes(StandardCharsets.UTF_8));
			org.w3c.dom.Document document = docBuilder.parse(stream);
			
			//<model><instance>
			Node root = document.getDocumentElement();
			Node modelNode = root.getFirstChild();
			Node instanceNode = modelNode.getFirstChild();
			
			//retrieve version and id
			String version = retrieveAttribute(instanceNode, "version");
			String id = retrieveAttribute(instanceNode, "id");
			form.setFormId(id);
			form.setFormVersion(version);
			
			//set the tableName
			String tableName = retrieveTableNameOrColForNode(instanceNode);
			form.setTableName(tableName);
			
			//process the xml model and create the respective tables 
            processNode(instanceNode, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return form;
	}
	
	public String retrieveTableNameOrColForNode(Node node){
		//replace all illegal xters in the table name 
		return "_" + node.getNodeName().toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
	}
	
	public static String retrieveAttribute(Node node, String attributeName) {
		// get a map containing the attributes of this node
		NamedNodeMap attributes = node.getAttributes();

		// get the number of nodes in this map
		int numAttrs = attributes.getLength();

		for (int i = 0; i < numAttrs; i++) {
			Attr attr = (Attr) attributes.item(i);
			
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();

			if (attrName.equals(attributeName)) {
				System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
				return attrValue;
			}
		}
		return null;
	}

	public void processNode(Node node, Node parentNode) {
		//replace all illegal xters in the table name 
		String tableName = retrieveTableNameOrColForNode(node);
		String parentName = parentNode != null ? node.getParentNode().getNodeName() : "";
		System.out.println(parentName + " : " + tableName);
		
		StringBuilder createTableSql = new StringBuilder("create table if not exists " + tableName + "(_id SERIAL PRIMARY KEY");// postgres specific!!!
		Map<String, String> alterTableSqlStatements = new HashMap<String, String>(); // alter table sql statements if necessary
		
		if (parentNode != null) {
            String foreignIdFieldName = retrieveTableNameOrColForNode(parentNode);
			createTableSql.append(", " + foreignIdFieldName  + " INTEGER DEFAULT NULL"); // Same table could be linking to multiple parents hence, we should allow null
            String alterStmt = "alter table " + tableName + " add column " + foreignIdFieldName + " INTEGER DEFAULT NULL";
            alterTableSqlStatements.put(foreignIdFieldName, alterStmt);
        }
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element
				String colName = retrieveTableNameOrColForNode(currentNode);
				createTableSql.append(", " + colName + " VARCHAR(500) DEFAULT NULL");
				String alterStmt = "alter table " + tableName + " add column " + colName + " VARCHAR(500) DEFAULT NULL";
				alterTableSqlStatements.put(colName, alterStmt);
				if (hasChildElements(currentNode)) {
					processNode(currentNode, node);
				}
			}
		}

		createTableSql.append(");");
		
		// execute sql statement to create the table
		try {
			executeCreateTableIfNotExistStatement(createTableSql.toString());
			executeAlterTableStatementsIfNecessarry(tableName, alterTableSqlStatements);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static boolean tableContainsColumn(String tableName, String columnName, Connection con) {
		try {
			String query = "SELECT * FROM " + tableName + " LIMIT 1";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columns = rsmd.getColumnCount();
				for (int i = 1; i <= columns; i++) {
					if (columnName.equalsIgnoreCase(rsmd.getColumnName(i))) {
						return true;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
     * Create the table if it doesn't exist
     */
    private void executeCreateTableIfNotExistStatement(final String createTableStmt) throws SQLException {
    	Connection connection = dataSource.getConnection();
    	PreparedStatement ps = connection.prepareStatement(createTableStmt);
		try {
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			ps.close();
		}
    }
	
	private void executeAlterTableStatementsIfNecessarry(final String tableName, final Map<String, String> alterTableSqlStatements){
        try {
        	Connection connection = dataSource.getConnection();
        	Set<String> keys = alterTableSqlStatements.keySet();
            for(String key : keys){
                String columnName = key;
                String alterTableSql = alterTableSqlStatements.get(key);
                if (!tableContainsColumn(tableName, columnName, connection)){
                	PreparedStatement ps = connection.prepareStatement(alterTableSql);
                	try{
                    	ps.execute();
                    }
                    catch (Exception e){
                    	e.printStackTrace(); 
                    }
                	finally {
                		ps.close();
					}
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public static boolean hasChildElements(Node el) {
	    NodeList children = el.getChildNodes();
	    for (int i = 0;i < children.getLength();i++) {
	        if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private void disableSSLCertCheck() throws NoSuchAlgorithmException, KeyManagementException {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

}
