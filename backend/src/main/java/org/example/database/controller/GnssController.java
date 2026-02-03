package org.example.database.controller;

import org.example.database.entity.Gnss;
import org.example.database.service.GnssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gnss")
public class GnssController {

    private  final GnssService gnssService;

    @Autowired
    public  GnssController(GnssService gnssService) {
        this.gnssService = gnssService;
    }

    @GetMapping("/")
    public List<Gnss> getAllGnss() {
        return gnssService.findAllGnss();
    }

    @GetMapping("/{id}")
    public Gnss getGnssById(@PathVariable Long id) {
        return gnssService.findGnssById(id);
    }

    @GetMapping("/timestamp")
    public List<Gnss> getGnssByTimestampUtcBetween(@RequestParam int start, @RequestParam int end) {
        return gnssService.findGnssByTimestampUtcBetween(start, end);
    }
}
