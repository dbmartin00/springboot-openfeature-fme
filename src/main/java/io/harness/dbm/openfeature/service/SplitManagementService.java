package io.harness.dbm.openfeature.service;

import io.harness.dbm.openfeature.config.FeatureFlagProperties;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;
import io.split.client.SplitManager;
import io.split.client.api.SplitView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "feature-flag.provider", havingValue = "split")
public class SplitManagementService {

    private static final Logger log = LoggerFactory.getLogger(SplitManagementService.class);

    private final FeatureFlagProperties properties;
    private SplitManager splitManager;

    public SplitManagementService(FeatureFlagProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() throws Exception {
        log.info("Initializing Split manager");
        SplitClientConfig config = SplitClientConfig.builder()
                .setBlockUntilReadyTimeout(properties.getBlockUntilReadyTimeoutMs())
                .build();

        splitManager = SplitFactoryBuilder.build(properties.getSplit().getApiKey(), config).manager();
        splitManager.blockUntilReady();
        log.info("Split manager is ready");
    }

    public List<SplitView> getAllSplits() {
        return splitManager.splits();
    }

    public List<SplitView> getSplitsStartingWith(String prefix) {
        return splitManager.splits().stream()
                .filter(view -> view.name.startsWith(prefix))
                .collect(Collectors.toList());
    }

    public SplitView getSplit(String name) {
        return splitManager.split(name);
    }
}
