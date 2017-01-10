/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.presence.dao;

import com.presence.sql.SQLBuilder;
import com.presence.util.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chauh
 */
public class DAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DAO.class);

    public static int insert(String keys, String tableName) throws SQLException {
        int status = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String sql = SQLBuilder.buildInsertQuery(tableName, Utility.tokenizeSingleJsonArray(keys));
            connection = DAOConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            status = preparedStatement.executeUpdate();
            //logger.debug("{} Record inserted", status);
  return status;          
        } catch (SQLException e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } finally {
            DAOConnectionFactory.closeConnection(connection, preparedStatement, null);
        }
    }

    public static int update(String keys, String filters, String tableName) throws SQLException {
        int status = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String sql = SQLBuilder.buildUpdateQuery(tableName, Utility.convertJSONToSQL(keys, null), Utility.convertJSONToSQL(filters, Utility.FILTER));
            connection = DAOConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            status = preparedStatement.executeUpdate();
            //logger.debug("{} Record updated", status);
            return status;
        } catch (SQLException e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } finally {
            DAOConnectionFactory.closeConnection(connection, preparedStatement, null);
            return status;
        }
    }

    public static String select(String keys, String filters, String tableName) throws SQLException {
//        long start = System.nanoTime();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String selectKeys = Utility.convertJSONToSQL(keys, Utility.SELECT);
            String sql = SQLBuilder.buildSelectQuery(tableName, selectKeys, Utility.convertJSONToSQL(filters, Utility.FILTER));
            
            connection = DAOConnectionFactory.getConnection();
  //          logger.debug("getConnection time: {}",(float)(System.nanoTime() - start)/1000000);
            preparedStatement = connection.prepareStatement(sql);
    //        logger.debug("prepareStatement time: {}",(float)(System.nanoTime() - start)/1000000);
            resultSet = preparedStatement.executeQuery();
      //      logger.debug("executeQuery time: {}",(float)(System.nanoTime() - start)/1000000);
            if (!resultSet.isBeforeFirst()) {
        //        logger.debug("DAO time: {}",(float)(System.nanoTime() - start)/1000000);
                return null;
            }
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//
//            while (resultSet.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) {
//                        System.out.print(",  ");
//                    }
//                    String columnValue = resultSet.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }
          //  logger.debug("DAO time: {}",(float)(System.nanoTime() - start)/1000000);
            return Utility.resultsetToJson(resultSet, selectKeys.equals("*")? null: selectKeys.split(","));

        } catch (SQLException e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating preparedstatement", e);
            throw e;
        } finally {
            DAOConnectionFactory.closeConnection(connection, preparedStatement, resultSet);
        }
    }

    public static int delete(String keys, String filters, String tableName) {
        int status = 0;
        return status;
    }

}
