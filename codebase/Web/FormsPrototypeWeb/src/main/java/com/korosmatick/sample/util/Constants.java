package com.korosmatick.sample.util;

public class Constants {

	public static class SyncType{
		//1 new record, 2 = update, 3 = new child record, 4 delete, 5 delete child record
		public static final int NEW_RECORD_CREATION = 1;
		public static final int UPDATE = 2;
		public static final int NEW_CHILD_RECORD = 3;
		public static final int DELETE = 4;
		public static final int CHILD_RECORD_DELETED = 5;
	}
	
}
