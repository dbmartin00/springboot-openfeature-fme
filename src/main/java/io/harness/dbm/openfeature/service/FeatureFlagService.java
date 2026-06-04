package io.harness.dbm.openfeature.service;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.TrackingEventDetails;
import dev.openfeature.sdk.Value;

import java.util.Map;

public interface FeatureFlagService {

    Boolean getBooleanFlag(String key, Boolean defaultValue, EvaluationContext context);

    String getStringFlag(String key, String defaultValue, EvaluationContext context);

    FlagEvaluationDetails<String> getStringFlagDetails(String key, String defaultValue, EvaluationContext context);

    Integer getIntegerFlag(String key, Integer defaultValue, EvaluationContext context);

    Double getDoubleFlag(String key, Double defaultValue, EvaluationContext context);

    EvaluationContext createContext(String targetingKey, Map<String, Value> attributes);

    boolean isReady();

    void trackEvent(String eventName, EvaluationContext context, TrackingEventDetails details);
}
