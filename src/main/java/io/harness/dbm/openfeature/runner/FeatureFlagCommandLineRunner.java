package io.harness.dbm.openfeature.runner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.MutableTrackingEventDetails;
import dev.openfeature.sdk.Value;
import io.harness.dbm.openfeature.service.FeatureFlagService;
import io.harness.dbm.openfeature.service.SplitManagementService;
import io.split.client.api.SplitView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeatureFlagCommandLineRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagCommandLineRunner.class);

    private final FeatureFlagService featureFlagService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private SplitManagementService splitManagementService;

    public FeatureFlagCommandLineRunner(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @Override
    public void run(String... args) {
        log.info("=== Feature Flag Demo Starting ===");

        if (splitManagementService != null) {
            log.info("--- Split Management ---");
            List<SplitView> splits = splitManagementService.getSplitsStartingWith("multivariant_demo");
            log.info("Splits starting with 'multivariant_demo':");
            for (SplitView view : splits) {
                log.info("  {} | trafficType={} | configs={}", view.name, view.trafficType, view.configs);
            }
        }

        log.info("--- Flag Evaluation ---");

        Map<String, Value> attributes = new HashMap<>();
        attributes.put("row", new Value("c"));
        EvaluationContext context = featureFlagService.createContext("c5", attributes);

        Boolean boolValue = featureFlagService.getBooleanFlag("new_onboarding", false, context);
        log.info("new_onboarding: {}", boolValue);

        String stringValue = featureFlagService.getStringFlag("multivariant_demo", "default", context);
        log.info("multivariant_demo: {}", stringValue);

        EvaluationContext ctx2 = featureFlagService.createContext("dmartin", null);

        FlagEvaluationDetails<String> details =
                featureFlagService.getStringFlagDetails("multivariant_demo", "fallback", ctx2);

        String dynamicConfig = details.getFlagMetadata().getString("config");

        log.info("multivariant_demo details - value: {}, reason: {}, dynamicConfig: {}",
                details.getValue(), details.getReason(), dynamicConfig);

        FlagEvaluationDetails<String> numDetails =
                featureFlagService.getStringFlagDetails("openfeaturetest", "fallback", ctx2);

        String numConfig = numDetails.getFlagMetadata().getString("config");

        parseAndLogNumericConfig(numConfig);

        // Track a custom event
        log.info("=== Tracking Custom Event ===");
        Map<String, Value> trackAttributes = new HashMap<>();
        trackAttributes.put("trafficType", new Value("user")); // Required by Split SDK

        EvaluationContext trackContext = featureFlagService.createContext(
            "user-track-demo",
            trackAttributes
        );

        MutableTrackingEventDetails trackDetails = new MutableTrackingEventDetails(100.0); // numeric value
        trackDetails.add("currency", "USD");
        trackDetails.add("product", "premium-plan");
        trackDetails.add("quantity", 1);

        featureFlagService.trackEvent("purchase", trackContext, trackDetails);
        log.info("Custom event 'purchase' tracked successfully");

        log.info("=== Feature Flag Demo Complete ===");
    }

    /**
     * Handles numeric JSON values safely:
     * - integers → long
     * - decimals → double
     * - non-numeric JSON → logged and ignored
     */
    private void parseAndLogNumericConfig(String json) {
        if (json == null) {
            log.info("config is null");
            return;
        }

        try {
            JsonNode node = objectMapper.readTree(json);

            if (!node.isNumber()) {
                log.info("config is not numeric. type={}", node.getNodeType());
                return;
            }

            if (node.isIntegralNumber()) {
                long value = node.longValue();
                log.info("config is INTEGER: {}", value);
            } else {
                double value = node.doubleValue();
                log.info("config is DOUBLE: {}", value);
            }

        } catch (Exception e) {
            log.error("Failed to parse config JSON: {}", json, e);
        }
    }
}
