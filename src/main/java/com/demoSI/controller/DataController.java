package com.demoSI.controller;

import com.demoSI.dto.DataRequest;
import com.demoSI.gateway.DataProcessingGateway;
import com.demoSI.model.ProcessedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demoSI")
public class DataController {

    @Autowired
    private DataProcessingGateway dataProcessingGateway;

    /**
     * Endpoint to trigger data processing
     * POST http://localhost:8081/api/demoSI/process
     */
    @PostMapping("/process")
    public ResponseEntity<String> processData(@RequestBody DataRequest request) {

        //  call the gateway
        dataProcessingGateway.processData(request.getDataId());

        return ResponseEntity.accepted()
                .body("Data processing started for ID: " + request.getDataId());
    }

    /**
     * Endpoint that waits for result
     */
    @PostMapping("/process-sync")
    public ResponseEntity<ProcessedData> processDataSync(@RequestBody DataRequest request) {

        ProcessedData result = dataProcessingGateway.processDataWithResult(request.getDataId());

        return ResponseEntity.ok(result);
    }
}
