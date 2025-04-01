package com.nashm.ultimate.ultimate_frisbee_ai;

import com.nashm.ultimate.ultimate_frisbee_ai.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class AIController {
    private final AIService aiService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, String> request) {
        try {
            String query = request.getOrDefault("query", "");
            if (query.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Query cannot be empty"));
            }

            // Call the AI service to process the query
            String response = aiService.processQuery(query);

            Map<String, Object> result = new HashMap<>();
            result.put("id", "1"); // This would be a database ID in a full implementation
            result.put("question", query);
            result.put("response", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error processing query: " + e.getMessage())
            );
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> saveFeedback(@RequestBody Map<String, Object> feedback) {
        try {
            // In a real implementation, you would save this feedback to your database
            return ResponseEntity.ok(Map.of("status", "Feedback received"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error saving feedback: " + e.getMessage())
            );
        }
    }
}
