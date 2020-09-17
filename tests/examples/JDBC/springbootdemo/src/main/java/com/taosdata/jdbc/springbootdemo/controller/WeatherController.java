package com.taosdata.jdbc.springbootdemo.controller;

import com.taosdata.jdbc.springbootdemo.domain.Weather;
import com.taosdata.jdbc.springbootdemo.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RequestMapping("/weather")
@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * create database and table
     *
     * @return
     */
    @GetMapping("/init")
    public boolean init() {
        return weatherService.init();
    }

    /**
     * Pagination Query
     *
     * @param limit
     * @param offset
     * @return
     */
    @GetMapping("/{limit}/{offset}")
    public List<Weather> queryWeather(@PathVariable Long limit, @PathVariable Long offset) {
        return weatherService.query(limit, offset);
    }

    /**
     * upload single weather info
     *
     * @param temperature
     * @param humidity
     * @return
     */
    @PostMapping("/{temperature}/{humidity}")
    public int saveWeather(@PathVariable int temperature, @PathVariable float humidity) {

        return weatherService.save(temperature, humidity);
    }

    /**
     * upload multi weather info
     *
     * @param weatherList
     * @return
     */
    @PostMapping("/batch")
    public int batchSaveWeather(@RequestBody List<Weather> weatherList) {
        return weatherService.save(weatherList);
    }

    @GetMapping("/batchInsertTest")
    public int batchInsertTest(@RequestParam("records") int records, @RequestParam("batchSize") int batchSize) {
        List<Weather> weatherList = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        Random random = new Random(startTime);
        for (int i = 0; i < records; i++) {
            Weather weather = new Weather();
            weather.setTs(new Timestamp(new Date(startTime++).getTime()));
            weather.setTemperature(random.nextInt(50));
            weather.setHumidity(random.nextInt(100));
            weatherList.add(weather);
        }

        int rowCnt = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < records; i += batchSize) {
            rowCnt += weatherService.save(weatherList.subList(i, i + batchSize));
        }
        long end = System.currentTimeMillis();
        System.out.println("Time Cost: " + (end - start) + " ms.");

        return rowCnt;
    }

}
