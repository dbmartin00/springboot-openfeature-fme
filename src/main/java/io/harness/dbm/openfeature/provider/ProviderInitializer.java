package io.harness.dbm.openfeature.provider;

import dev.openfeature.sdk.OpenFeatureAPI;
import io.harness.dbm.openfeature.config.FeatureFlagProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ProviderInitializer {

    private static final Logger log = LoggerFactory.getLogger(ProviderInitializer.class);

    private final OpenFeatureAPI api;
    private final FeatureFlagProperties properties;
    private volatile boolean ready = false;

    public ProviderInitializer(OpenFeatureAPI api, FeatureFlagProperties properties) {
        this.api = api;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeProvider() {
        log.info("Application ready, waiting for feature flag provider to be ready...");
        try {
            api.getProviderMetadata();
            ready = true;
            log.info("Feature flag provider is ready");
        } catch (Exception e) {
            log.error("Error waiting for feature flag provider to be ready", e);
            ready = false;
        }
    }

    public boolean isReady() {
        return ready;
    }
}
