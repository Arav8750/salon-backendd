package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiInsightService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Value("${huggingface.api.url}")
    private String hfApiUrl;

    public ApiResponse<Map<String, Object>> generateInsights() {

        // 1. Collect statistics from DB
        long totalAppointments = appointmentRepository.count();

        List<Object[]> serviceStats = appointmentRepository.findServicePopularity();
        String topService = serviceStats.isEmpty() ? "N/A" : (String) serviceStats.get(0)[0];

        List<Object[]> barberStats = appointmentRepository.findBarberPerformance();
        String topBarber = barberStats.isEmpty() ? "N/A" : (String) barberStats.get(0)[0];

        List<Object[]> dayStats = appointmentRepository.findBusiestDay();
        String busiestDay = dayStats.isEmpty() ? "N/A" : (String) dayStats.get(0)[0];

        // 2. Build prompt
        String prompt = "You are a business analyst for a salon.\n\n" +
                "Analyze the salon statistics below and generate three short insights for the salon owner.\n\n" +
                "Salon Data:\n" +
                "Total Appointments: " + totalAppointments + "\n" +
                "Most Popular Service: " + topService + "\n" +
                "Top Barber: " + topBarber + "\n" +
                "Busiest Day: " + busiestDay + "\n\n" +
                "Provide exactly three insights in bullet points.";

        // 3. Call HuggingFace API
        String insights = callHuggingFace(prompt);

        // 4. Build response payload
        Map<String, Object> result = new HashMap<>();
        result.put("totalAppointments", totalAppointments);
        result.put("topService", topService);
        result.put("topBarber", topBarber);
        result.put("busiestDay", busiestDay);
        result.put("insights", insights);

        return ApiResponse.success("AI insights generated successfully", result);
    }

    private String callHuggingFace(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // ✅ Get token from environment
            String token = System.getenv("HF_TOKEN");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            // Create request body
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "meta-llama/Llama-3.1-8B-Instruct:cerebras");
            requestBody.put("messages", List.of(userMessage));
            requestBody.put("max_tokens", 400);
            requestBody.put("temperature", 0.7);
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    hfApiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getBody() != null) {
                Object choicesObj = response.getBody().get("choices");
                if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                    Object firstChoice = choices.get(0);
                    if (firstChoice instanceof Map<?, ?> choiceMap) {
                        Object messageObj = choiceMap.get("message");
                        if (messageObj instanceof Map<?, ?> messageMap) {
                            Object content = messageMap.get("content");
                            if (content != null) {
                                return content.toString().trim();
                            }
                        }
                    }
                }
            }

            return "Unable to generate insights at this time. Please try again later.";

        } catch (Exception e) {
            return "Error contacting AI service: " + e.getMessage();
        }
    }
}