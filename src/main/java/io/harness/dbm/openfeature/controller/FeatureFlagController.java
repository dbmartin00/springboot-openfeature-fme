package io.harness.dbm.openfeature.controller;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.Value;
import io.harness.dbm.openfeature.service.FeatureFlagService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/flags")
@ConditionalOnProperty(name = "feature-flag.rest-api.enabled", havingValue = "true", matchIfMissing = false)
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("ready", featureFlagService.isReady());
        response.put("status", featureFlagService.isReady() ? "UP" : "DOWN");
        return response;
    }

    @GetMapping("/evaluate/boolean")
    public Map<String, Object> evaluateBoolean(
            @RequestParam String flagKey,
            @RequestParam(defaultValue = "false") Boolean defaultValue,
            @RequestParam String userId,
            @RequestParam(required = false) String row) {

        Map<String, Value> attributes = new HashMap<>();
        if (row != null) {
            attributes.put("row", new Value(row));
        }
        EvaluationContext context = featureFlagService.createContext(userId, attributes);

        Boolean value = featureFlagService.getBooleanFlag(flagKey, defaultValue, context);

        Map<String, Object> response = new HashMap<>();
        response.put("flagKey", flagKey);
        response.put("value", value);
        response.put("userId", userId);
        return response;
    }

    @GetMapping("/evaluate/string")
    public Map<String, Object> evaluateString(
            @RequestParam String flagKey,
            @RequestParam(defaultValue = "default") String defaultValue,
            @RequestParam String userId,
            @RequestParam(required = false) String row) {

        Map<String, Value> attributes = new HashMap<>();
        if (row != null) {
            attributes.put("row", new Value(row));
        }
        EvaluationContext context = featureFlagService.createContext(userId, attributes);

        FlagEvaluationDetails<String> details = featureFlagService.getStringFlagDetails(flagKey, defaultValue, context);

        Map<String, Object> response = new HashMap<>();
        response.put("flagKey", flagKey);
        response.put("value", details.getValue());
        response.put("reason", details.getReason());
        response.put("userId", userId);

        String config = details.getFlagMetadata().getString("config");
        if (config != null) {
            response.put("config", config);
        }

        return response;
    }
}
