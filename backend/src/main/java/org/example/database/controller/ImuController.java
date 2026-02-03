package org.example.database.controller;

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
}
