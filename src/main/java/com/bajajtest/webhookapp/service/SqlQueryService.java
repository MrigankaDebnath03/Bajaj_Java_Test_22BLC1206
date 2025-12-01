package com.bajajitest.webhookapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryService.class);

    /**
     * Determine which SQL query to use based on registration number
     * Odd regNo -> Question 1
     * Even regNo -> Question 2
     */
    public String getSqlQuery(String regNo) {
        logger.info("Determining SQL query for regNo: {}", regNo);

        // Extract last two digits from registration number
        String digits = regNo.replaceAll("[^0-9]", "");
        
        if (digits.isEmpty()) {
            logger.error("No digits found in registration number: {}", regNo);
            throw new IllegalArgumentException("Invalid registration number format");
        }

        int lastTwoDigits = Integer.parseInt(digits.substring(Math.max(0, digits.length() - 2)));
        logger.info("Last two digits: {}", lastTwoDigits);

        boolean isOdd = lastTwoDigits % 2 != 0;
        logger.info("Registration number is: {}", isOdd ? "ODD" : "EVEN");

        if (isOdd) {
            return getQuestion1Query();
        } else {
            return getQuestion2Query();
        }
    }

    /**
     * SQL Query for Question 1 (Odd regNo)
     * REPLACE THIS WITH YOUR ACTUAL SQL SOLUTION FOR QUESTION 1
     */
    private String getQuestion1Query() {
        logger.info("Using Question 1 SQL Query");
        
        // TODO: Replace with your actual SQL solution for Question 1
        String query = """
                SELECT 
                    column1, 
                    column2, 
                    COUNT(*) as count
                FROM 
                    table_name
                WHERE 
                    condition = 'value'
                GROUP BY 
                    column1, column2
                ORDER BY 
                    count DESC
                LIMIT 10
                """.trim();
        
        return query;
    }

    /**
     * SQL Query for Question 2 (Even regNo)
     * REPLACE THIS WITH YOUR ACTUAL SQL SOLUTION FOR QUESTION 2
     */
    private String getQuestion2Query() {
        logger.info("Using Question 2 SQL Query");
        
        // TODO: Replace with your actual SQL solution for Question 2
        String query = """
                SELECT 
                    column1, 
                    column2, 
                    SUM(column3) as total
                FROM 
                    table_name
                WHERE 
                    condition = 'value'
                GROUP BY 
                    column1, column2
                ORDER BY 
                    total DESC
                LIMIT 10
                """.trim();
        
        return query;
    }
}