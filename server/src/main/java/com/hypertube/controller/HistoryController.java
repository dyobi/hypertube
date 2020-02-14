package com.hypertube.controller;

import com.hypertube.model.Response;
import com.hypertube.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
public class HistoryController {

    @Autowired
    HistoryService historyService;

    @GetMapping("/history/{movieId}")
    public Response getHistory(@RequestParam String token, @PathVariable("movieId") Long movieId) {
        return historyService.getHistory(token, movieId);
    }

    @GetMapping("/histories/{userName}")
    public Response getHistories(@RequestParam String token, @PathVariable("userName") String userName) {
        return historyService.getHistories(token, userName);
    }

    @PostMapping("/history")
    public Response postHistory(@RequestBody String token, @RequestBody Long movieId, @RequestBody int current, @RequestBody int duration) {
        return historyService.postHistory(token, movieId, current, duration);
    }

}
