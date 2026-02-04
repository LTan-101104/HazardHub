# HazardHub

HazardHub is a comprehensive safety platform that helps communities prepare for, respond to, and recover from emergencies — from severe weather events to everyday civic hazards.

# Set up Firebase

To run with Firebase utilities, copy the file containing Firebase credentials to hub/src/main/java/hazardHub/com/hub/resources and rename the file to firebase-service-account.json

SOS Event (Tan)
id (UUID, PK)
user_id (UUID, FK → User, NOT NULL)
trip_id (UUID, FK → Trip, NULLABLE) — May not be on a trip
triggered_at (TIMESTAMP, NOT NULL)
resolved_at (TIMESTAMP, NULLABLE)
latitude (DECIMAL 10,8, NOT NULL)
longitude (DECIMAL 11,8, NOT NULL)
location_accuracy_meters (FLOAT, NULLABLE)
status (ENUM, NOT NULL, DEFAULT 'active') — active, help_arriving, resolved
contacts_notified (JSONB, NOT NULL, DEFAULT '[]') - stored in JSON, no model defined
dispatch_notified (BOOLEAN, DEFAULT false)
dispatch_reference (VARCHAR 100, NULLABLE) — Case number
