package com.ly723.db.helper;

import android.database.sqlite.SQLiteDatabase;

/**
 * a transction support class
 */
public class DBTransaction {

	private DBTransaction() {
	}
	
	/**
	 * executes sqls in a transction
	 */
	public static void transact(DbSQLite db, DBTransactionInterface transctionInterface){
		if(transctionInterface!=null){
			SQLiteDatabase sqliteDb = db.getSQLiteDatabase();
			sqliteDb.beginTransaction();
			try{
				transctionInterface.onTransact();
				sqliteDb.setTransactionSuccessful();
			}finally{
				sqliteDb.endTransaction();
			}
		}
	}
	
	public interface DBTransactionInterface{
   	 void onTransact();
   }
}
