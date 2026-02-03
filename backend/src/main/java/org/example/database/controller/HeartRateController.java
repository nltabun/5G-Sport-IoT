package org.example.database.controller;

import org.example.database.entity.Ecg;
import org.example.database.entity.HeartRate;
import org.example.database.service.HeartRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heartrate")
public class HeartRateController {

    private final HeartRateService heartRateService;

    @Autowired
    public HeartRateController(HeartRateService heartRateService) {
        this.heartRateService = heartRateService;
    }

    @GetMapping("/")
    private List<HeartRate> getHeartRates() {
        return heartRateService.findAllHeartRates();
    }

    @GetMapping("/{id}")
    public HeartRate getHeartRateById(@PathVariable Long id){
        return heartRateService.findHeartRateById(id);
    }

    @GetMapping("/timestamp")
    public List<HeartRate> getHeartRatesByTimestampUtcBetween(@RequestParam int start, @RequestParam int end) {
        return heartRateService.findHeartRateByTimestampUtcBetween(start, end);
    }

        // Api call /api/gnss/last?window=X
    // X = requested hours to the past.
    @GetMapping("/last")
    public List<HeartRate> getLastWindowHours(
            @RequestParam(defaultValue = "1") int window
    ) {
        if (window <= 0) {
            throw new IllegalArgumentException("window must be > 0! ");
        }

        // Maximum time in hours
        if (window > 168) { // max 7 days
            throw new IllegalArgumentException("window must be max 168 / 7 days");
        }

        int end = (int) (System.currentTimeMillis() / 1000L);
        int start = end - window * 3600;

        return heartRateService.findHeartRateByTimestampUtcBetween(start, end);
    }

}
