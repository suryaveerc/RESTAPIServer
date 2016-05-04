package com.presence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;

/**
 * Created by Suryaveer on 7/20/2015.
 */
public class DAOConnectionFactory {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DAOConnectionFactory.class);
    private static final String databaseJNDI = "jdbc/presencedb";
    private static DataSource dataSource = null;
    private DAOConnectionFactory(){}
    static {

        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + databaseJNDI);
        } catch (NamingException e) {
            logger.error("Error while creating datasource.", e);
        }

    }

    protected static Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }

    protected static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Error while closing resultset.", e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Error while closing statement.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error while closing connection.", e);
            }
        }
    }
}
