package com.nashm.ultimate.ultimate_frisbee_ai.service;

import com.nashm.ultimate.ultimate_frisbee_ai.model.Query;
import com.nashm.ultimate.ultimate_frisbee_ai.repository.QueryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final RestTemplate restTemplate;
    private final QueryRepository queryRepository;

    @Value("${ai.model.url}")
    private String aiModelUrl;

    public Query processQuery(String questionText) {
        logger.info("Processing query: {}", questionText);
        try {
            // Prepare the request to the Ollama API wrapper
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question", questionText);
            requestBody.put("context", ""); // In a real implementation, you would add relevant context

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            logger.debug("Calling Ollama API at URL: {}", aiModelUrl);
            // Make the request to the Ollama API wrapper
            Map<String, Object> response = restTemplate.postForObject(aiModelUrl, request, Map.class);

            String answer;
            if (response != null && response.containsKey("answer")) {
                answer = response.get("answer").toString();
                logger.debug("Received answer from Ollama API: {}", answer.substring(0, Math.min(100, answer.length())) + "...");
            } else {
                answer = "I'm sorry, I couldn't generate a response at this time.";
                logger.warn("No answer received from Ollama API");
            }

            // Save the query and response to the database
            Query query = new Query();
            query.setQuestion(questionText);
            query.setAnswer(answer);
            query.setCreatedAt(LocalDateTime.now());

            Query savedQuery = queryRepository.save(query);
            logger.info("Saved query to database with ID: {}", savedQuery.getId());

            return savedQuery;
        } catch (Exception e) {
            logger.error("Error calling Ollama API for question: {}", questionText, e);

            // Even on error, save the query to database with error message
            Query query = new Query();
            query.setQuestion(questionText);
            query.setAnswer("Error processing query: " + e.getMessage());
            query.setCreatedAt(LocalDateTime.now());

            Query savedQuery = queryRepository.save(query);
            logger.info("Saved error query to database with ID: {}", savedQuery.getId());

            return savedQuery;
        }
    }

    public void saveFeedback(Long queryId, Integer rating, String feedback) {
        logger.info("Saving feedback for query ID {}: rating={}, feedback={}", queryId, rating, feedback);
        queryRepository.findById(queryId).ifPresent(query -> {
            query.setRating(rating);
            query.setUserFeedback(feedback);
            queryRepository.save(query);
            logger.info("Feedback saved for query ID: {}", queryId);
        });
    }
}