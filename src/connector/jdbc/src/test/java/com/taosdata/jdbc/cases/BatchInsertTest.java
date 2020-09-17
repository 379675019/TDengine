package com.taosdata.jdbc.cases;

import com.taosdata.jdbc.lib.TSDBCommon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BatchInsertTest {

    static String host = "localhost";
    static String dbName = "test";
    final static int numOfRecordsPerTable = 100000;
    final static int valuesSize = 1;
    static long ts = 1496732686000l;

    private Connection connection;

    @Before
    public void before() {
        try {
            connection = TSDBCommon.getConn(host);
            TSDBCommon.createDatabase(connection, dbName);
            try (Statement statement = connection.createStatement()) {
                statement.execute("create table if not exists " + dbName + ".weather(ts timestamp, temperature float, humidity int)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Random rand = new Random(System.currentTimeMillis());

    @Test
    public void testBatchInsert() {
        int count = 0;
        int rowCnt = 0;
        long startTime = System.currentTimeMillis();
        while (count < numOfRecordsPerTable) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO " + dbName + ".weather(ts, temperature, humidity) VALUES");
            for (int j = 0; j < valuesSize; j++) {
                sb.append("(" + (ts + count++) + ", ");
                sb.append(rand.nextInt(50) + ", ");
                sb.append(rand.nextInt(100) + ")");
            }
//            System.out.println(sb.toString());
            rowCnt += execute(sb.toString());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("insert " + rowCnt + " records with time cost: " + (endTime - startTime) + " microseconds");
    }

    private int execute(String sql) {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @After
    public void after() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
