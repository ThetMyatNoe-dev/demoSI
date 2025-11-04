package com.demoSI.config;
import com.demoSI.activator.DataServiceActivator;
import com.demoSI.filter.DataFilter;
import com.demoSI.router.DataRouter;
import com.demoSI.service.DataWritingService;
import com.demoSI.service.NotificationService;
import com.demoSI.transformer.DataTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.demoSI")
public class CompleteIntegrationConfig {

    private final DataTransformer dataTransformer;
    private final DataFilter dataFilter;
    private final DataRouter dataRouter;
    private final DataServiceActivator dataServiceActivator;
    private final DataWritingService dataWritingService;
    private final NotificationService notificationService;

    public CompleteIntegrationConfig(DataTransformer dataTransformer,
                                     DataFilter dataFilter,
                                     DataRouter dataRouter,
                                     DataServiceActivator dataServiceActivator,
                                     DataWritingService dataWritingService,
                                     NotificationService notificationService) {
        this.dataTransformer = dataTransformer;
        this.dataFilter = dataFilter;
        this.dataRouter = dataRouter;
        this.dataServiceActivator = dataServiceActivator;
        this.dataWritingService = dataWritingService;
        this.notificationService = notificationService;
    }

    /**
     * MAIN INTEGRATION FLOW
     * Complete flow with all components:
     * Transform → Filter → Route → Process → Enrich → Write → Notify
     */
    @Bean
    public IntegrationFlow completeDataProcessingFlow() {
        return IntegrationFlow
                // 1. Entry point
                .from("dataInputChannel")

                // 2. TRANSFORMER: Convert String to RawData
                .transform("dataTransformer", "transformStringToRawData")
                .channel("transformedChannel")

                // 3. TRANSFORMER: Normalize data
                .transform("dataTransformer", "normalizeData")
                .channel("normalizedChannel")

                // 4. TRANSFORMER: Add headers
                .transform("dataTransformer", "addHeaders")
                .channel("enrichedHeaderChannel")

                // 5. FILTER: Validate data
                .filter(dataFilter, "validateRawData",
                        e -> e.discardChannel("invalidDataChannel"))
                .channel("validatedChannel")

                // 6. FILTER: Check timestamp
                .filter(dataFilter, "filterByTimestamp",
                        e -> e.discardChannel("oldDataChannel"))
                .channel("recentDataChannel")

                // 7. ROUTER: Route by source to appropriate processor
                .route("dataRouter", "routeBySource")

                // Note: Routed messages come back to processedChannel
                // via service activators in DataServiceActivator

                .get();
    }

    /**
     * POST-PROCESSING FLOW
     * Handles processed data
     */
    @Bean
    public IntegrationFlow postProcessingFlow() {
        return IntegrationFlow
                .from("processedChannel")

                // 8. SERVICE ACTIVATOR: Enrich data
                .handle("dataServiceActivator", "enrichData")
                .channel("enrichedChannel")

                // 9. FILTER: Only successful processing
                .filter(dataFilter, "filterSuccessfulProcessing",
                        e -> e.discardChannel("errorChannel"))
                .channel("successChannel")

                // 10. Write to database
                .handle("dataWritingService", "write")
                .channel("writtenDataChannel")

                // 11. Send notification
                .handle("notificationService", "notify")

                .get();
    }

    /**
     * PRIORITY ROUTING FLOW
     * Separate flow for priority-based routing
     */
    @Bean
    public IntegrationFlow priorityRoutingFlow() {
        return IntegrationFlow
                .from("priorityInputChannel")

                // Route by priority
                .route("dataRouter", "routeByPriority")

                .get();
    }

    /**
     * CATEGORY ROUTING FLOW
     * Multi-channel routing by category
     */
    @Bean
    public IntegrationFlow categoryRoutingFlow() {
        return IntegrationFlow
                .from("categoryInputChannel")

                // Route to multiple channels
                .route("dataRouter", "routeByCategory")

                .get();
    }

    // ============================================
    // Channel Definitions
    // ============================================

    @Bean
    public MessageChannel dataInputChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel transformedChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel normalizedChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel enrichedHeaderChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel validatedChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel recentDataChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel databaseProcessingChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel apiProcessingChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel fileProcessingChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel processedChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel enrichedChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel successChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel writtenDataChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel errorChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel invalidDataChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel oldDataChannel() { return new DirectChannel(); }
    @Bean
    public MessageChannel financialAuditChannel() { return new DirectChannel(); }
}