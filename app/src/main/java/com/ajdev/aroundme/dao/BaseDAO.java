package com.ajdev.aroundme.dao;

/**
 * Created by Akshay.Jayakumar on 10/12/2017.
 *
 * This class forms the base of other Data Access Objects (DAO) in this application.
 *
 * deleteFromDB() will be implemented by other DAOs to ensure that data is deleted from
 * Realm DB.
 */

abstract class BaseDAO {
    abstract public void deleteFromDB();
}
