package com.korosmatick.sample.dao;

import java.util.List;

import com.korosmatick.sample.model.db.DbChangeLogTransaction;

public interface DbChangeLogTransactionDao {
	
	DbChangeLogTransaction findById(Long id);
	void add(DbChangeLogTransaction dbChangeLogTransaction);
	void delete(DbChangeLogTransaction dbChangeLogTransaction);
	void deleteById(Long id);
	List<DbChangeLogTransaction> findAll();
	
}
