# SpringBoot OpenFeature FME SDK Example

Java SpringBoot architecture to test flags and segments.

Export FME server-side API key

```
export SPLIT_API_KEY=<your server-side api key>
```

Run
```
mvn spring-boot:run '-Dspring-boot.run.profiles=dev'
```

Flags include a multivariant_demo (three treatments with JSON config) and new_onboarding (boolean)


