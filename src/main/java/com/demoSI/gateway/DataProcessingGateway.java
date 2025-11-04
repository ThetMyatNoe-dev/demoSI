package com.demoSI.gateway;

import com.demoSI.model.ProcessedData;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Gateway provides a simple interface to trigger the integration flow
 * Hides all the messaging complexity from calling code
 */
@MessagingGateway
public interface DataProcessingGateway {

    /**
     * Send data for processing
     * This method automatically sends message to dataInputChannel
     * and starts the entire flow
     */
    @Gateway(requestChannel = "dataInputChannel")
    void processData(String dataId);

    /**
     * You can also have a method that waits for the result
     */
    @Gateway(requestChannel = "dataInputChannel", replyTimeout = 10000)
    ProcessedData processDataWithResult(String dataId);
}