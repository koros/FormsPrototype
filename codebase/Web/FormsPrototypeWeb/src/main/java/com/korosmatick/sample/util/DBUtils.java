package com.korosmatick.sample.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

public class DBUtils {

	/**
	 * convert a single sql row to map
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static Map<String , String> resultSetToMap(ResultSet rs) throws Exception{
		Map<String,String> map = new HashMap<String, String>();
		ResultSetMetaData meta = rs.getMetaData();
		try {
			if (rs.next()) {
				for (int i = 1; i <= meta.getColumnCount(); i++) {
	                String key = meta.getColumnName(i);
	                String value = rs.getString(key);
	                map.put(key, value);
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String , String> resultSetToMap(ResultSet rs, int row) throws Exception{
		Map<String,String> map = new HashMap<String, String>();
		ResultSetMetaData meta = rs.getMetaData();
		try {
			if (rs.absolute(row)) {
				for (int i = 1; i <= meta.getColumnCount(); i++) {
	                String key = meta.getColumnName(i);
	                String value = rs.getString(key);
	                map.put(key, value);
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
