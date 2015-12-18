package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.DbChangeLog;

public interface DbChangeLogDao {
	DbChangeLog findById(Long id);
	void add(DbChangeLog dbChangeLog);
	void delete(DbChangeLog dbChangeLog);
	void deleteById(Long id);
	List<DbChangeLog> findAll();
}
