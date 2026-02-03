package org.example.database.controller;

import org.example.database.entity.Ecg;
import org.example.database.entity.Imu;
import org.example.database.service.ImuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imu")
public class ImuController {
    private final ImuService imuService;

    @Autowired
    public ImuController(ImuService imuService) {
        this.imuService = imuService;
    }

    @GetMapping("/")
    public List<Imu> getAllImu(){
        return imuService.findAllImu();
    }

    @GetMapping("/{id}")
    public Imu getImuById(@PathVariable Long id){
        return imuService.findImuById(id);
    }

    @GetMapping("/timestamp")
    public List<Imu> getImuByTimestampUtcBetween(@RequestParam int start, @RequestParam int end) {
        return imuService.findImuByTimestampUtcBetween(start, end);
    }

        // Api call /api/gnss/last?window=X
    // X = requested hours to the past.
    @GetMapping("/last")
    public List<Imu> getLastWindowHours(
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

        return imuService.findImuByTimestampUtcBetween(start, end);
    }

}
