package io.harness.dbm.openfeature.runner;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.MutableContext;
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
        FlagEvaluationDetails<String> details = featureFlagService.getStringFlagDetails("multivariant_demo", "fallback", ctx2);
        String dynamicConfig = details.getFlagMetadata().getString("config");
        log.info("multivariant_demo details - value: {}, reason: {}, dynamicConfig: {}",
                 details.getValue(), details.getReason(), dynamicConfig);

        log.info("=== Feature Flag Demo Complete ===");
    }
}
