package com.taosdata.jdbc.example.jdbcTemplate;


import com.taosdata.jdbc.example.jdbcTemplate.dao.ExecuteAsStatement;
import com.taosdata.jdbc.example.jdbcTemplate.dao.WeatherDao;
import com.taosdata.jdbc.example.jdbcTemplate.domain.Weather;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class BatcherInsertTest {


    @Autowired
    private WeatherDao weatherDao;
    @Autowired
    private ExecuteAsStatement executor;

    private static final int numOfRecordsPerTable = 10;
    private static final int batchSize = 2;

    private static Random random = new Random(System.currentTimeMillis());

    @Before
    public void before() {
        // drop database
        executor.doExecute("drop database if exists test");
        // create database
        executor.doExecute("create database if not exists test");
        //use database
        executor.doExecute("use test");
        //use database
        executor.doExecute("drop table if exists test.weather");
        // create table
        executor.doExecute("create table if not exists test.weather (ts timestamp, temperature float, humidity int)");
    }

    @Test
    public void batchInsert() {
        long ts = System.currentTimeMillis();
        List<Weather> weatherList = new ArrayList<>();
        for (int i = 0; i < numOfRecordsPerTable; i++) {
            ts += 1000;
            Weather weather = new Weather(new Timestamp(ts), random.nextFloat() * 50.0f, random.nextInt(100));
            weatherList.add(weather);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < numOfRecordsPerTable; i += batchSize) {
            weatherDao.batchInsert(weatherList.subList(i, i + batchSize));
        }
        long end = System.currentTimeMillis();
        System.out.println("batch insert(" + numOfRecordsPerTable + " rows) time cost ==========> " + (end - start) + " ms");

        int count = weatherDao.count();
        assertEquals(count, numOfRecordsPerTable);
    }

}
