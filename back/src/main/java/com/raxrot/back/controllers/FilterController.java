package com.raxrot.back.controllers;

import com.raxrot.back.dtos.FilterRequestDTO;
import com.raxrot.back.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private final FilterService filterService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterRequestDTO filter) {
        return ResponseEntity.ok(filterService.filterTransactions(filter));
    }
}
