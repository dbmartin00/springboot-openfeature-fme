package io.harness.dbm.openfeature.service;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.TrackingEventDetails;
import dev.openfeature.sdk.Value;
import io.harness.dbm.openfeature.config.FeatureFlagProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagServiceImpl.class);

    private final Client client;
    private final FeatureFlagProperties properties;

    public FeatureFlagServiceImpl(Client client, FeatureFlagProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public Boolean getBooleanFlag(String key, Boolean defaultValue, EvaluationContext context) {
        try {
            Boolean value = client.getBooleanValue(key, defaultValue, context);
            log.debug("Boolean flag '{}' evaluated to: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Error evaluating boolean flag '{}', returning default value: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    @Override
    public String getStringFlag(String key, String defaultValue, EvaluationContext context) {
        try {
            String value = client.getStringValue(key, defaultValue, context);
            log.debug("String flag '{}' evaluated to: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Error evaluating string flag '{}', returning default value: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    @Override
    public FlagEvaluationDetails<String> getStringFlagDetails(String key, String defaultValue, EvaluationContext context) {
        try {
            FlagEvaluationDetails<String> details = client.getStringDetails(key, defaultValue, context);
            log.debug("String flag details for '{}': value={}, reason={}", key, details.getValue(), details.getReason());
            return details;
        } catch (Exception e) {
            log.error("Error evaluating string flag details '{}', returning default value: {}", key, defaultValue, e);
            return FlagEvaluationDetails.<String>builder()
                    .flagKey(key)
                    .value(defaultValue)
                    .build();
        }
    }

    @Override
    public Integer getIntegerFlag(String key, Integer defaultValue, EvaluationContext context) {
        try {
            Integer value = client.getIntegerValue(key, defaultValue, context);
            log.debug("Integer flag '{}' evaluated to: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Error evaluating integer flag '{}', returning default value: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    @Override
    public Double getDoubleFlag(String key, Double defaultValue, EvaluationContext context) {
        try {
            Double value = client.getDoubleValue(key, defaultValue, context);
            log.debug("Double flag '{}' evaluated to: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Error evaluating double flag '{}', returning default value: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    @Override
    public EvaluationContext createContext(String targetingKey, Map<String, Value> attributes) {
        MutableContext context = new MutableContext(targetingKey);
        if (attributes != null) {
            attributes.forEach((key, value) -> {
                if (value.isBoolean()) {
                    context.add(key, value.asBoolean());
                } else if (value.isString()) {
                    context.add(key, value.asString());
                } else if (value.isNumber()) {
                    context.add(key, value.asInteger());
                } else if (value.isStructure()) {
                    context.add(key, value.asStructure());
                } else if (value.isList()) {
                    context.add(key, value.asList());
                }
            });
        }
        return context;
    }

    @Override
    public boolean isReady() {
        try {
            client.getBooleanValue("_readiness_check", false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void trackEvent(String eventName, EvaluationContext context, TrackingEventDetails details) {
        try {
            client.track(eventName, context, details);
            log.info("Tracked event '{}' for context: {}", eventName, context.getTargetingKey());
        } catch (Exception e) {
            log.error("Error tracking event '{}'", eventName, e);
        }
    }
}
