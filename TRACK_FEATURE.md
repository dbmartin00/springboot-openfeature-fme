# OpenFeature Track Event Implementation

This document describes the Track event functionality added to the Spring Boot OpenFeature application.

## Overview

OpenFeature Java SDK v1.18.1 includes support for tracking custom events through the `Tracking` interface. The Split OpenFeature Provider (v1.2.1) implements this feature, allowing events to be sent to Harness Feature Management & Experimentation (FME) for analytics and experimentation.

## Implementation

### Service Layer

The `FeatureFlagService` interface now includes a `trackEvent` method:

```java
void trackEvent(String eventName, EvaluationContext context, TrackingEventDetails details);
```

This method:
- Takes an event name (e.g., "purchase", "signup")
- Requires an `EvaluationContext` with at least a targeting key and `trafficType` attribute
- Accepts optional `TrackingEventDetails` with numeric values and custom properties

### Command Line Demo

The `FeatureFlagCommandLineRunner` demonstrates tracking in the startup demo:

```java
Map<String, Value> trackAttributes = new HashMap<>();
trackAttributes.put("email", new Value("demo@example.com"));
trackAttributes.put("trafficType", new Value("user")); // Required by Split

EvaluationContext trackContext = featureFlagService.createContext(
    "user-track-demo",
    trackAttributes
);

MutableTrackingEventDetails trackDetails = new MutableTrackingEventDetails(100.0);
trackDetails.add("currency", "USD");
trackDetails.add("product", "premium-plan");
trackDetails.add("quantity", 1);

featureFlagService.trackEvent("purchase", trackContext, trackDetails);
```

### REST API Endpoint

A new POST endpoint is available at `/api/flags/track`:

**Parameters:**
- `eventName` (required) - Name of the event to track
- `targetingKey` (required) - User/entity identifier
- `trafficType` (optional) - Type of traffic (defaults to "user")
- `value` (optional) - Numeric value associated with the event
- Request body (optional JSON) - Custom properties to attach to the event

**Example:**
```bash
curl -X POST "http://localhost:8080/api/flags/track?eventName=purchase&targetingKey=user-123&value=99.99" \
  -H "Content-Type: application/json" \
  -d '{"currency": "USD", "product": "premium-plan", "quantity": 1}'
```

## Important: trafficType Requirement

The Split SDK requires a `trafficType` attribute in the evaluation context for all track calls. This can be provided:

1. As a query parameter: `?trafficType=user`
2. In the request body JSON: `{"trafficType": "customer"}`
3. Will default to "user" if not specified

## Verifying Events in Harness FME

After tracking events:

1. Log into your Harness account
2. Navigate to Feature Management → Events/Analytics
3. Look for tracked events with:
   - Event name (e.g., "purchase")
   - User/targeting key
   - Numeric value (if provided)
   - Custom properties

Note: Events may take a few minutes to appear due to batching and ingestion delays.

## Running the Demo

```bash
# Set your Split API key
export SPLIT_API_KEY=your-api-key-here

# Build and run
mvn clean package
java -jar target/openfeature-0.0.1-SNAPSHOT.jar
```

Look for the output:
```
=== Tracking Custom Event ===
Tracked event 'purchase' for context: user-track-demo
Custom event 'purchase' tracked successfully
```

## Technical Notes

- Track calls are **fire-and-forget** (void return) - they don't block or return results
- Errors are logged but don't throw exceptions
- Events are batched and sent asynchronously by the Split SDK
- The `value` field in `TrackingEventDetails` is optional
- Custom properties support: String, Integer, Double, Boolean types
