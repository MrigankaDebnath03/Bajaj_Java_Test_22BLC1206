package com.bajajitest.webhookapp.runner;

import com.bajajitest.webhookapp.model.WebhookRequest;
import com.bajajitest.webhookapp.model.WebhookResponse;
import com.bajajitest.webhookapp.service.SqlQueryService;
import com.bajajitest.webhookapp.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private SqlQueryService sqlQueryService;

    @Value("${user.name}")
    private String userName;

    @Value("${user.regNo}")
    private String userRegNo;

    @Value("${user.email}")
    private String userEmail;

    @Override
    public void run(String... args) {
        logger.info("=".repeat(80));
        logger.info("APPLICATION STARTED - Beginning Webhook Flow");
        logger.info("=".repeat(80));

        try {
            // Step 1: Generate Webhook
            logger.info("\n--- STEP 1: Generating Webhook ---");
            WebhookRequest request = new WebhookRequest(userName, userRegNo, userEmail);
            WebhookResponse webhookResponse = webhookService.generateWebhook(request);

            if (webhookResponse == null || webhookResponse.getWebhook() == null) {
                logger.error("Failed to receive webhook response");
                return;
            }

            // Step 2: Determine SQL Query based on regNo
            logger.info("\n--- STEP 2: Determining SQL Query ---");
            String sqlQuery = sqlQueryService.getSqlQuery(userRegNo);

            // Step 3: Submit Solution
            logger.info("\n--- STEP 3: Submitting Solution ---");
            String submissionResponse = webhookService.submitSolution(
                    webhookResponse.getWebhook(),
                    webhookResponse.getAccessToken(),
                    sqlQuery
            );

            logger.info("\n" + "=".repeat(80));
            logger.info("WORKFLOW COMPLETED SUCCESSFULLY!");
            logger.info("Final Response: {}", submissionResponse);
            logger.info("=".repeat(80));

        } catch (Exception e) {
            logger.error("\n" + "=".repeat(80));
            logger.error("WORKFLOW FAILED!");
            logger.error("Error: {}", e.getMessage(), e);
            logger.error("=".repeat(80));
        }
    }
}