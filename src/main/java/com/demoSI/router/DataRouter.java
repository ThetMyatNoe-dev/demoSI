package com.demoSI.router;

import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataRouter {

    private static final Logger log = LoggerFactory.getLogger(DataRouter.class);

    /**
     * ROUTER 1: Route by data source
     * Routes to different processors based on source
     */
    @Router(inputChannel = "routerChannel")
    public String routeBySource(RawData rawData) {
        log.info("ROUTER: Routing data ID: {} from source: {}",
                rawData.getId(), rawData.getSource());

        switch (rawData.getSource()) {
            case "DATABASE":
                log.info("ROUTER: Routing to DATABASE processor");
                return "databaseProcessingChannel";
            case "API":
                log.info("ROUTER: Routing to API processor");
                return "apiProcessingChannel";
            case "FILE":
                log.info("ROUTER: Routing to FILE processor");
                return "fileProcessingChannel";
            default:
                log.warn("ROUTER: Unknown source, routing to default");
                return "defaultProcessingChannel";
        }
    }

    /**
     * ROUTER 2: Route by priority
     * High priority goes to fast lane, others to normal lane
     */
    @Router(inputChannel = "priorityRouterChannel")
    public String routeByPriority(RawData rawData) {
        log.info("ROUTER: Routing by priority: {}", rawData.getBusinessPriority());

        if ("HIGH".equals(rawData.getBusinessPriority())) {
            log.info("ROUTER: HIGH priority - fast lane");
            return "fastProcessingChannel";
        } else {
            log.info("ROUTER: Normal priority - standard lane");
            return "standardProcessingChannel";
        }
    }

    /**
     * ROUTER 3: Route by category (multiple channels)
     * Can route to multiple destinations
     */
    @Router(inputChannel = "categoryRouterChannel")
    public List<String> routeByCategory(RawData rawData) {
        log.info("ROUTER: Routing by category: {}", rawData.getCategory());

        List<String> channels = new ArrayList<>();

        // All data goes to main processing
        channels.add("mainProcessingChannel");

        // Category-specific routing
        switch (rawData.getCategory()) {
            case "FINANCIAL":
                channels.add("financialAuditChannel");
                channels.add("complianceChannel");
                break;
            case "PERSONAL":
                channels.add("privacyCheckChannel");
                break;
            case "BUSINESS":
                channels.add("businessAnalyticsChannel");
                break;
        }

        log.info("ROUTER: Routing to {} channels", channels.size());
        return channels;
    }
}