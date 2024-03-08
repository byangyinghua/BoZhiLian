package com.ly723.db.helper;


import com.ly723.db.interfaces.IBaseDao;

/**
 * use this factory to create your sqlite data access object
 */
public class DaoFactory {
	
	/**
	 * call this method to new a GenericDao
	 */
	@SuppressWarnings("unchecked")
	public static <T> IBaseDao<T> createGenericDao(DbSQLite db, Class<?> modelClazz){
		return new GenericDao<T>(db,modelClazz);
	}  

}
