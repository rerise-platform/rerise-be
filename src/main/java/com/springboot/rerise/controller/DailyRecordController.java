package com.springboot.rerise.controller;

import com.springboot.rerise.dto.DailyRecordDto;
import com.springboot.rerise.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class DailyRecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<DailyRecordDto> createOrUpdateRecord(
            @Valid @RequestBody DailyRecordDto request) {
        DailyRecordDto response = recordService.createOrUpdateRecord(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<DailyRecordDto> getRecordByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyRecordDto response = recordService.getRecordByDate(date);
        return ResponseEntity.ok(response);
    }

/*
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<List<DailyRecordDto.CalendarResponse>> getCalendarData(
            @PathVariable int year,
            @PathVariable int month) {
        List<DailyRecordDto.CalendarResponse> response =
                recordService.getCalendarData(year, month);
        return ResponseEntity.ok(response);
    }
*/
}