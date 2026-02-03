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
    public List<Ecg> getEcgByTimestampUtcBetween(@RequestParam int start, @RequestParam int end) {
        return ecgService.findEcgByTimestampUtcBetween(start, end);
    }
}
