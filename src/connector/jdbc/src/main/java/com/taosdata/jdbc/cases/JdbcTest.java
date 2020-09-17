package com.taosdata.jdbc.cases;

import com.taosdata.jdbc.TSDBDriver;

import java.sql.*;
import java.util.Properties;

public class JdbcTest {

    public static void main(String[] args) {

        try {
            Class.forName("com.taosdata.jdbc.TSDBDriver");
            String jdbcUrl = "jdbc:TAOS://192.168.236.135:6030/";
            Properties connProps = new Properties();
            connProps.setProperty(TSDBDriver.PROPERTY_KEY_USER, "root");
            connProps.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, "taosdata");
            connProps.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
            connProps.setProperty(TSDBDriver.PROPERTY_KEY_LOCALE, "en_US.UTF-8");
            connProps.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, "UTC-8");

            Connection connection = DriverManager.getConnection(jdbcUrl, connProps);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from test.t");
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.print(metaData.getColumnLabel(i) + ": " + resultSet.getString(i)+"\t");
                }
                System.out.println();
            }
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
