package com.nashm.ultimate.ultimate_frisbee_ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {
    private final RestTemplate restTemplate;

    @Value("${ai.model.url}")
    private String aiModelUrl;

    public String processQuery(String query) {
        try {
            // Prepare the request to the Ollama API wrapper
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question", query);
            requestBody.put("context", ""); // In a real implementation, you would add relevant context

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make the request to the Ollama API wrapper
            Map<String, Object> response = restTemplate.postForObject(aiModelUrl, request, Map.class);

            if (response != null && response.containsKey("answer")) {
                return response.get("answer").toString();
            } else {
                return "I'm sorry, I couldn't generate a response at this time.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, there was an error processing your request: " + e.getMessage();
        }
    }
}
