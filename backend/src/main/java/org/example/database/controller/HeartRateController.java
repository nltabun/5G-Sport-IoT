package org.example.database.controller;

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
}
