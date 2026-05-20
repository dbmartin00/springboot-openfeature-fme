package io.harness.dbm.openfeature;

import java.lang.reflect.Field;
import java.util.Map;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.ImmutableMetadata;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.Structure;
import dev.openfeature.sdk.Value;
import dev.openfeature.sdk.Metadata;

import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;
import io.split.client.SplitManager;
import io.split.client.api.SplitView;
import io.split.openfeature.SplitProvider;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	String apiKey = System.getenv("SPLIT_API_KEY");
    	if (apiKey == null || apiKey.isEmpty()) {
    		System.err.println("ERROR: SPLIT_API_KEY environment variable is required");
    		System.exit(1);
    	}

    	OpenFeatureAPI api = OpenFeatureAPI.getInstance();

    	SplitClientConfig config = SplitClientConfig.builder()
    	   .setBlockUntilReadyTimeout(10000)
    	   .build();

    	SplitClient splitClient = SplitFactoryBuilder.build(apiKey, config).client();
    	splitClient.blockUntilReady();

    	api.setProvider(new SplitProvider(splitClient));
		Client client = api.getClient("my-app");

		SplitManager splitManager = SplitFactoryBuilder.build(apiKey, config).manager();
		splitManager.blockUntilReady();
		System.out.println("splits - ");
		for(SplitView view : splitManager.splits()) {
			if(view.name.startsWith("multivariant_demo")) {
				System.out.println(view.name + " " + view.trafficType + " " + view.configs);
			}
		}
		
		MutableContext context = new MutableContext("c5");
		context.add("row", "c");
		
		Boolean boolValue = client.getBooleanValue("new_onboarding", false, context);
		System.out.println("new_onboarding: " + boolValue);
		
		String stringValue = client.getStringValue("multivariant_demo", "default", context);
		System.out.println("multivariant_demo: " + stringValue);
		
//		Client client = api.getClient();
		EvaluationContext ctx = new MutableContext("dmartin");

		FlagEvaluationDetails<String> details = client.getStringDetails("multivariant_demo", "fallback", ctx);
		String dynamicConfig = details.getFlagMetadata().getString("config");
		System.out.println("dynamicConfig: " + dynamicConfig);
    }
}
