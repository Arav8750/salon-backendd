package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.service.AiInsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiInsightService aiInsightService;

    @GetMapping("/insights")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInsights() {
        return ResponseEntity.ok(aiInsightService.generateInsights());
    }
}
