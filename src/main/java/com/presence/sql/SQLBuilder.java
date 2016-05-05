/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presence.sql;

import com.presence.util.Utility;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chauh
 */
public class SQLBuilder {
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SQLBuilder.class);
    public static String buildSelectQuery(String tableName, String keys, String filter) {
        //long start = System.nanoTime();
        StringBuilder query = new StringBuilder();
        if (filter != null) {
            query.append(SQLPredicates.SELECT).append(keys).append(SQLPredicates.FROM).append(tableName).append(SQLPredicates.WHERE).append(filter);
        } else {
            query.append(SQLPredicates.SELECT).append(keys).append(SQLPredicates.FROM).append(tableName);
        }
        //logger.debug("Select: {}", query.toString());
    //logger.debug("buildSelectQuery time: {}",(float)(System.nanoTime() - start)/1000000);
        return query.toString();
    }

    public static String buildInsertQuery(String tableName, Map<String, String> keyValues) {
        StringBuilder query = new StringBuilder();
        List<String> kv = Utility.mapToList(keyValues);
        String keys = kv.get(0);
        String values =kv.get(1);
        query.append(SQLPredicates.INSERT).append(tableName).append(keys).append(SQLPredicates.VALUES).append(values);
      //  logger.debug("Insert {}", query.toString());
        return query.toString();
    }

    public static String buildUpdateQuery(String tableName, String keys, String filter) {
        StringBuilder query = new StringBuilder();
        if (filter != null) {
            query.append(SQLPredicates.UPDATE).append(tableName).append(SQLPredicates.SET).append(keys).append(SQLPredicates.WHERE).append(filter);
        } else {
            query.append(SQLPredicates.UPDATE).append(tableName).append(SQLPredicates.SET).append(keys);
        }
     //  logger.debug("Update {}",query.toString());
        return query.toString();
    }

    public static String buildDeleteQuery(String tableName, String keys, String filter) {
        StringBuilder query = new StringBuilder();
        if (filter != null) {
            query.append(SQLPredicates.DELETE).append(SQLPredicates.FROM).append(tableName).append(SQLPredicates.WHERE).append(filter);
        } else {
            query.append(SQLPredicates.DELETE).append(SQLPredicates.FROM).append(tableName);
        }
        //logger.debug("Delete {}"+query.toString());
        return query.toString();
    }
}
