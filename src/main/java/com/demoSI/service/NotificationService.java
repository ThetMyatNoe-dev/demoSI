package com.demoSI.service;

import com.demoSI.model.ProcessedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Sends notification after data is written
     * Could be email, SMS, webhook, message queue, etc.
     */
    public void notify(ProcessedData processedData) {
        log.info("Sending notification for data ID: {}", processedData.getId());

        try {
            // Simulate notification sending
            Thread.sleep(200);

            // In real scenario:
            if ("SUCCESS".equals(processedData.getStatus())) {
                sendSuccessNotification(processedData);
            } else {
                sendFailureNotification(processedData);
            }

            log.info("Notification sent successfully for: {}", processedData.getId());

        } catch (Exception e) {
            log.error("Error sending notification for: {}", processedData.getId(), e);
            // Don't throw exception - notification failure shouldn't break the flow
        }
    }

    private void sendSuccessNotification(ProcessedData data) {
        // Send email, push notification, webhook, etc.
        log.info("SUCCESS notification: Data {} processed in {}ms",
                data.getId(), data.getProcessingTime());
    }

    private void sendFailureNotification(ProcessedData data) {
        // Alert team about failure
        log.warn("FAILURE notification: Data {} processing failed", data.getId());
    }
}