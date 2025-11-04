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
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataProcessingGateway dataProcessingGateway;

    /**
     * Test different data sources
     * DB-123 -> routes to database processor
     * API-456 -> routes to API processor
     * FILE-789 -> routes to file processor
     */
    @PostMapping("/process")
    public ResponseEntity<String> processData(@RequestBody DataRequest request) {
        dataProcessingGateway.processData(request.getDataId());
        return ResponseEntity.accepted()
                .body("Data processing started for ID: " + request.getDataId());
    }

    @PostMapping("/process-sync")
    public ResponseEntity<ProcessedData> processDataSync(@RequestBody DataRequest request) {
        ProcessedData result = dataProcessingGateway.processDataWithResult(request.getDataId());
        return ResponseEntity.ok(result);
    }
}
