package org.example.database.controller;

import org.example.database.entity.Gnss;
import org.example.database.service.GnssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Instant;

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
    public List<Gnss> getGnssByTimestampUtcBetween(@RequestParam Long start, @RequestParam Long end) {
        return gnssService.findGnssByTimestampUtcBetween(start, end);
    }

    // Api call /api/gnss/last?window=X
    // X = requested hours to the past.
    @GetMapping("/last")
    public List<Gnss> getLastWindowHours(
            @RequestParam(defaultValue = "1") int window
    ) {
        if (window <= 0) {
            throw new IllegalArgumentException("window must be > 0! ");
        }

        // Maximum time in hours
        if (window > 168) { // max 7 days
            throw new IllegalArgumentException("window must be max 168 / 7 days");
        }

        Instant now = Instant.now();
        Long end = (now.getEpochSecond() * 1_000_000_000L) + now.getNano();
        Long start = end - (window * 3_600_000_000_000L);

        return gnssService.findGnssByTimestampUtcBetween(start, end);
    }
}
