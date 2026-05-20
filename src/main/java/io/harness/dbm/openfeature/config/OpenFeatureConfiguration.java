package io.harness.dbm.openfeature.config;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;
import io.harness.dbm.openfeature.provider.ProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeatureFlagProperties.class)
public class OpenFeatureConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OpenFeatureConfiguration.class);

    @Bean
    public OpenFeatureAPI openFeatureAPI(ProviderFactory providerFactory) {
        log.info("Initializing OpenFeatureAPI singleton");
        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProviderAndWait(providerFactory.createProvider());
        return api;
    }

    @Bean
    public Client featureFlagClient(OpenFeatureAPI api, FeatureFlagProperties properties) {
        log.info("Creating OpenFeature client with name: {}", properties.getClientName());
        return api.getClient(properties.getClientName());
    }
}
