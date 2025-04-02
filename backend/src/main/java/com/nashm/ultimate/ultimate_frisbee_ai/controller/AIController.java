package com.nashm.ultimate.ultimate_frisbee_ai.controller;

import com.nashm.ultimate.ultimate_frisbee_ai.model.QueryRequest;
import com.nashm.ultimate.ultimate_frisbee_ai.service.AIService;
import com.nashm.ultimate.ultimate_frisbee_ai.model.Query;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "AI Assistant", description = "Ultimate Frisbee AI Assistant API")
public class AIController {
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    private final AIService aiService;

    @PostMapping("/query")
    @Operation(summary = "Submit a question to the AI",
            description = "Processes a question about Ultimate Frisbee and returns an AI-generated response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully processed query"),
            @ApiResponse(responseCode = "400", description = "Invalid query"),
            @ApiResponse(responseCode = "500", description = "Error processing query")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Query request",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Object.class),
                    examples = {
                            @ExampleObject(
                                    name = "Basic Query",
                                    summary = "A simple query example",
                                    value = "{ \"query\": \"What are the basic rules of Ultimate Frisbee?\" }"
                            )
                    }
            )
    )
    public ResponseEntity<Map<String, Object>> query(
            @RequestBody QueryRequest request) {
        String questionText = request.getQuery();
        logger.info("Received question: {}", questionText);

        try {
            if (questionText.isEmpty()) {
                logger.warn("Empty query received");
                return ResponseEntity.badRequest().body(Map.of("error", "Query cannot be empty"));
            }

            // Call the AI service to process the query and save to database
            logger.debug("Processing query through AI service");
            Query savedQuery = aiService.processQuery(questionText);
            logger.info("Query processed and saved with ID: {}", savedQuery.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("id", savedQuery.getId());
            result.put("question", savedQuery.getQuestion());
            result.put("response", savedQuery.getAnswer());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error processing query: {}", questionText, e);
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error processing query: " + e.getMessage())
            );
        }
    }

    @PostMapping("/feedback")
    @Operation(summary = "Submit feedback for a response", description = "Saves user feedback about the quality of an AI response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback saved successfully"),
            @ApiResponse(responseCode = "500", description = "Error saving feedback")
    })
    public ResponseEntity<Map<String, String>> saveFeedback(
            @Parameter(description = "The feedback object")
            @RequestBody Map<String, Object> request) {
        try {
            Long queryId = Long.valueOf(request.get("queryId").toString());
            Integer rating = Integer.valueOf(request.get("rating").toString());
            String feedback = request.getOrDefault("feedback", "").toString();

            logger.info("Received feedback for query ID {}: rating={}", queryId, rating);

            // Save feedback to the database
            aiService.saveFeedback(queryId, rating, feedback);
            logger.info("Feedback saved successfully for query ID: {}", queryId);

            return ResponseEntity.ok(Map.of("status", "Feedback saved successfully"));
        } catch (Exception e) {
            logger.error("Error saving feedback", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error saving feedback: " + e.getMessage())
            );
        }
    }
}