📋 Main Flow (Happy Path):
dataInputChannel → Read → Process → Filter → Write → Notify ✓

🎯Error Path (Failed Processing):
Filter Rejection → errorChannel → Retry Logic → retryChannel → dataInputChannel (loop back)

Dead Letter Path (Max Retries Exceeded):
errorChannel → deadLetterChannel → Log & Alert

-----------------------------------------------------------------------------------------------


When call dataProcessingGateway.processData(request.getDataId()): Example calling like below

dataProcessingGateway.processData("DATA123");

** Step1 - Gateway Proxy Intercepts**

// Spring's proxy intercepts and does:
Message<String> message = MessageBuilder
    .withPayload("DATA123")  // Your dataId
    .build();

// Then sends to the channel specified in @Gateway
dataInputChannel.send(message);
```
**Step 2: Flow Execution Begins for Happy Case **
```
Message arrives at dataInputChannel
    ↓
┌─────────────────────────────────────────┐
│ Handler 1: dataReadingService.readData()│
│ Input: "DATA123"                        │
│ Output: RawData object                  │
└─────────────────────────────────────────┘
    ↓
Message sent to readDataChannel
    ↓
┌─────────────────────────────────────────┐
│ Handler 2: dataProcessingService.process│
│ Input: RawData                          │
│ Output: ProcessedData object            │
└─────────────────────────────────────────┘
    ↓
Message sent to processedDataChannel
    ↓
┌─────────────────────────────────────────┐
│ Filter: Check status                    │
│ If status == "SUCCESS": continue        │
│ If status != "SUCCESS": → errorChannel  │
└─────────────────────────────────────────┘
    ↓ (if SUCCESS)
Message sent to writtenDataChannel
    ↓
┌─────────────────────────────────────────┐
│ Handler 3: dataWritingService.write()   │
│ Input: ProcessedData                    │
│ Output: WrittenData                     │
└─────────────────────────────────────────┘
    ↓
Message sent to writtenDataChannel
    ↓
┌─────────────────────────────────────────┐
│ Handler 4: notificationService.notify() │
│ Input: WrittenData                      │
│ Output: (void or notification result)   │
└─────────────────────────────────────────┘
    ↓
Flow Complete ✓


## 📊 **Complete Message Journey Diagram**
```
┌─────────────────────────────────────────────────────────────┐
│                     HAPPY PATH                               │
│  Gateway → Read → Process → Filter → Write → Notify ✓       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   RETRY PATH (retryCount < 3)                │
│                                                              │
│  Filter REJECT → errorChannel                                │
│       ↓                                                      │
│  handleError (check retryCount)                              │
│       ↓                                                      │
│  Create retry message (increment retryCount)                 │
│       ↓                                                      │
│  retryChannel                                                │
│       ↓                                                      │
│  dataInputChannel (LOOP BACK TO START)                       │
│       ↓                                                      │
│  Repeat flow...                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                DEAD LETTER PATH (retryCount >= 3)            │
│                                                              │
│  Filter REJECT → errorChannel                                │
│       ↓                                                      │
│  handleError (check retryCount)                              │
│       ↓                                                      │
│  retryCount >= 3 → deadLetterChannel                         │
│       ↓                                                      │
│  handleDeadLetter (log, alert, persist)                      │
│       ↓                                                      │
│  END (no more retries)                                       │