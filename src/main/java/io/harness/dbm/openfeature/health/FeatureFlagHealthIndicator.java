package io.harness.dbm.openfeature.health;

import io.harness.dbm.openfeature.config.FeatureFlagProperties;
import io.harness.dbm.openfeature.service.FeatureFlagService;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagHealthIndicator extends AbstractHealthIndicator {

    private final FeatureFlagService service;
    private final FeatureFlagProperties properties;

    public FeatureFlagHealthIndicator(FeatureFlagService service, FeatureFlagProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        if (service.isReady()) {
            builder.up()
                    .withDetail("provider", properties.getProvider())
                    .withDetail("clientName", properties.getClientName())
                    .withDetail("status", "ready");
        } else {
            builder.down()
                    .withDetail("provider", properties.getProvider())
                    .withDetail("clientName", properties.getClientName())
                    .withDetail("status", "not ready");
        }
    }
}
