package org.example.database.controller;

import org.example.database.entity.Ecg;

import org.example.database.service.EcgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecg")
public class EcgController {
    private final EcgService ecgService;

    @Autowired
    public EcgController(EcgService ecgService) {
        this.ecgService = ecgService;
    }

    @GetMapping("/")
    public List<Ecg> getAllEcg() {
        return ecgService.findAllEcg();
    }

    @GetMapping("/{id}")
    public Ecg getEcgById(@PathVariable Long id) {
        return ecgService.findEcgById(id);
    }

    @GetMapping("/timestamp")
    public List<Ecg> getEcgByTimestampUtcBetween(@RequestParam Long start, @RequestParam Long end) {
        return ecgService.findEcgByTimestampUtcBetween(start, end);
    }

    // Api call /api/gnss/last?window=X
    // X = requested hours to the past.
    @GetMapping("/last")
    public List<Ecg> getLastWindowHours(
            @RequestParam(defaultValue = "1") int window
    ) {
        if (window <= 0) {
            throw new IllegalArgumentException("window must be > 0! ");
        }

        // Maximum time in hours
        if (window > 168) { // max 7 days
            throw new IllegalArgumentException("window must be max 168 / 7 days");
        }

        Long end = System.nanoTime();
        Long start = end - (window * 3600 * 1000000000L);

        return ecgService.findEcgByTimestampUtcBetween(start, end);
    }
}
