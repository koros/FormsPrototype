package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.RequestsLogs;

public interface RequestsLogsDao {
	RequestsLogs findById(Long id);
	void add(RequestsLogs requestsLogs);
	void delete(RequestsLogs requestsLogs);
	void deleteById(Long id);
	List<RequestsLogs> findAll();
}
