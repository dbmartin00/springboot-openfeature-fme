package io.harness.dbm.openfeature.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "feature-flag")
@Validated
public class FeatureFlagProperties {

    private ProviderType provider = ProviderType.SPLIT;
    private String clientName = "my-app";

    @NotNull
    private Integer blockUntilReadyTimeoutMs = 10000;

    private SplitConfig split = new SplitConfig();
    private FlagdConfig flagd = new FlagdConfig();

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getBlockUntilReadyTimeoutMs() {
        return blockUntilReadyTimeoutMs;
    }

    public void setBlockUntilReadyTimeoutMs(Integer blockUntilReadyTimeoutMs) {
        this.blockUntilReadyTimeoutMs = blockUntilReadyTimeoutMs;
    }

    public SplitConfig getSplit() {
        return split;
    }

    public void setSplit(SplitConfig split) {
        this.split = split;
    }

    public FlagdConfig getFlagd() {
        return flagd;
    }

    public void setFlagd(FlagdConfig flagd) {
        this.flagd = flagd;
    }

    public static class SplitConfig {
        @NotBlank
        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public static class FlagdConfig {
        private String host = "localhost";
        private Integer port = 8013;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
}
