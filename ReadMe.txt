Expected Flow:

✅ Transformer: Converts to RawData with source="DATABASE"
✅ Filter: Validates data
✅ Router: Routes to databaseProcessingChannel
✅ Service Activator: processDatabaseData() processes it
✅ Result: [DB-PROCESSED]


TRANSFORMER: Converting dataId DB-FIN-URGENT-123 to RawData object
TRANSFORMER: Created RawData with source=DATABASE, priority=HIGH
FILTER: Validating data for ID: DB-FIN-URGENT-123
FILTER: Data ACCEPTED for ID: DB-FIN-URGENT-123
ROUTER: Routing data ID: DB-FIN-URGENT-123 from source: DATABASE
ROUTER: Routing to DATABASE processor
SERVICE ACTIVATOR: Processing DATABASE data for ID: DB-FIN-URGENT-123

📋 Component Summary
Transformers (3 types)

String → RawData: Converts input string to object
Normalize: Cleans and standardizes data
Add Headers: Enriches message with metadata

Filters (4 types)

Validate: Checks content validity
Timestamp: Filters old data
Success: Only allows successful processing
Priority: Filters by priority level

Routers (3 types)

By Source: Routes to DATABASE/API/FILE processors
By Priority: Routes to fast/standard lanes
By Category: Routes to multiple channels (audit, compliance, etc.)

Service Activators (8 types)

Database Processor: Processes database data
API Processor: Processes API data
File Processor: Processes file data
Data Enrichment: Adds metadata
Error Handler: Handles failures
Invalid Data Handler: Handles validation failures
Old Data Archiver: Archives old data
Audit Logger: Logs financial data


