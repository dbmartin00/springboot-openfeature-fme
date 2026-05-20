package io.harness.dbm.openfeature.provider;

import dev.openfeature.contrib.providers.flagd.FlagdOptions;
import dev.openfeature.contrib.providers.flagd.FlagdProvider;
import dev.openfeature.sdk.FeatureProvider;
import io.harness.dbm.openfeature.config.FeatureFlagProperties;
import io.harness.dbm.openfeature.config.ProviderType;
import io.split.openfeature.SplitProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(ProviderFactory.class);

    private final FeatureFlagProperties properties;

    public ProviderFactory(FeatureFlagProperties properties) {
        this.properties = properties;
    }

    public FeatureProvider createProvider() {
        log.info("Creating feature flag provider of type: {}", properties.getProvider());

        if (properties.getProvider() == ProviderType.SPLIT) {
            return createSplitProvider();
        } else if (properties.getProvider() == ProviderType.FLAGD) {
            return createFlagdProvider();
        } else {
            throw new IllegalArgumentException("Unknown provider type: " + properties.getProvider());
        }
    }

    private FeatureProvider createSplitProvider() {
        String apiKey = properties.getSplit().getApiKey();
        log.info("Initializing Split provider with API key: {}...",
                 apiKey.substring(0, Math.min(8, apiKey.length())));
        return new SplitProvider(apiKey);
    }

    private FeatureProvider createFlagdProvider() {
        FlagdOptions options = FlagdOptions.builder()
                .host(properties.getFlagd().getHost())
                .port(properties.getFlagd().getPort())
                .build();
        log.info("Initializing flagd provider at {}:{}",
                 properties.getFlagd().getHost(),
                 properties.getFlagd().getPort());
        return new FlagdProvider(options);
    }
}
