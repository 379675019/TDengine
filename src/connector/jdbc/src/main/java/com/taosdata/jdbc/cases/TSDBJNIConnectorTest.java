package com.taosdata.jdbc.cases;

import com.taosdata.jdbc.TSDBConstants;
import com.taosdata.jdbc.TSDBJNIConnector;
import com.taosdata.jdbc.TSDBResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class TSDBJNIConnectorTest {

    public static void main(String[] args) {
        try {
            // init
            TSDBJNIConnector.init(null, "en_US.UTF-8", "UTF-8", "UTC-8");
            // connect
            TSDBJNIConnector connector = new TSDBJNIConnector();
            connector.connect("192.168.236.135", 6030, "test", "root", "taosdata");
            // query
            String sql = "describe test.t";
            long pSql = connector.executeQuery(sql);
            // handle the resultSet
            long resultSetPointer = connector.getResultSet();

            if (resultSetPointer == TSDBConstants.JNI_CONNECTION_NULL) {
                connector.freeResultSet(pSql);
                throw new SQLException(TSDBConstants.FixErrMsg(TSDBConstants.JNI_CONNECTION_NULL));
            }

            // create/insert/update/delete/alter
            if (resultSetPointer == TSDBConstants.JNI_NULL_POINTER) {
                connector.freeResultSet(pSql);
            }

            // select
            if (!connector.isUpdateQuery(pSql)) {
                TSDBResultSet resultSet = new TSDBResultSet(connector, resultSetPointer);
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        System.out.print(metaData.getColumnLabel(i) + " : " + resultSet.getString(i) + "\t");
                    }
                    System.out.println();
                }
            } else {
                connector.freeResultSet(pSql);
            }

        } catch (SQLWarning throwables) {
            throwables.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}