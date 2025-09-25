package com.kaaj.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/date")
public class DateController {

    @GetMapping
    public LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }
}