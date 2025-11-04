package com.demoSI.gateway;

import com.demoSI.model.ProcessedData;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Gateway provides a simple interface to trigger the integration flow
 * Hides all the messaging complexity from calling code
 */
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface DataProcessingGateway {

    @Gateway(requestChannel = "dataInputChannel")
    void processData(String dataId);

    @Gateway(requestChannel = "dataInputChannel", replyTimeout = 10000)
    ProcessedData processDataWithResult(String dataId);
}