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
            String sql = SQLBuilder.buildInsertQuery(tableName, Utility.tokenizeSingleJsonArray(keys) );
            connection = DAOConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            status = preparedStatement.executeUpdate();
            logger.debug("{} Record inserted", status);
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
            String sql = SQLBuilder.buildUpdateQuery(tableName, Utility.convertJSONToSQL(keys,null), Utility.convertJSONToSQL(filters, Utility.FILTER));
            connection = DAOConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            status = preparedStatement.executeUpdate();
            logger.debug("{} Record updated", status);
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

    public static String select(String keys, String filters, String tableName) throws SQLException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        StringBuilder outputJson = null;
        ResultSet resultSet = null;

        try {
            String selectKeys = Utility.convertJSONToSQL(keys, Utility.SELECT);
            logger.debug("selectkeys: {}", selectKeys);
            for (String retval : selectKeys.split(",")) {
                logger.debug(retval);
            }
            String sql = SQLBuilder.buildSelectQuery(tableName, selectKeys, Utility.convertJSONToSQL(filters, Utility.FILTER));
            connection = DAOConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                return null;
            }
            return Utility.resultsetToJson(resultSet, selectKeys.split(","));

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
