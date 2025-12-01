package com.bajajitest.webhookapp.service;

import com.bajajitest.webhookapp.model.SolutionRequest;
import com.bajajitest.webhookapp.model.WebhookRequest;
import com.bajajitest.webhookapp.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String baseUrl;

    @Value("${api.generate.webhook}")
    private String generateWebhookPath;

    @Value("${api.submit.solution}")
    private String submitSolutionPath;

    /**
     * Generate webhook by sending POST request
     */
    public WebhookResponse generateWebhook(WebhookRequest request) {
        String url = baseUrl + generateWebhookPath;
        
        logger.info("Generating webhook for regNo: {}", request.getRegNo());
        logger.info("POST URL: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    WebhookResponse.class
            );

            WebhookResponse webhookResponse = response.getBody();
            
            if (webhookResponse != null) {
                logger.info("Webhook generated successfully!");
                logger.info("Webhook URL: {}", webhookResponse.getWebhook());
                logger.info("Access Token: {}", webhookResponse.getAccessToken());
            }

            return webhookResponse;

        } catch (HttpClientErrorException e) {
            logger.error("HTTP Error during webhook generation: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate webhook: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error generating webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate webhook: " + e.getMessage(), e);
        }
    }

    /**
     * Submit solution to webhook URL with JWT token
     */
    public String submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        logger.info("Submitting solution to webhook: {}", webhookUrl);
        logger.info("SQL Query: {}", sqlQuery);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            logger.info("Solution submitted successfully!");
            logger.info("Response: {}", responseBody);

            return responseBody;

        } catch (HttpClientErrorException e) {
            logger.error("HTTP Error during solution submission: {} - {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to submit solution: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error submitting solution: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit solution: " + e.getMessage(), e);
        }
    }
}