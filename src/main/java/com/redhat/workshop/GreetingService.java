package com.redhat.workshop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GreetingService {

    private static final Logger LOG = Logger.getLogger(GreetingService.class);
    private static final String DATASET_PATH = "data/greetings.json";

    private volatile Map<String, String> greetings = Map.of();

    void onStart(@Observes StartupEvent event) {
        loadGreetings();
    }

    Optional<Greeting> findGreeting(String countryCode) {
        return Optional.ofNullable(normalize(countryCode))
                .flatMap(code -> Optional.ofNullable(greetings.get(code))
                        .map(message -> new Greeting(code, message)));
    }

    private void loadGreetings() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DATASET_PATH)) {
            if (stream == null) {
                throw new IllegalStateException("Missing greeting dataset: " + DATASET_PATH);
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> loaded = mapper.readValue(stream, new TypeReference<Map<String, String>>() { });
            greetings = loaded.entrySet().stream()
                    .collect(Collectors.toUnmodifiableMap(
                            entry -> entry.getKey().toUpperCase(Locale.ROOT),
                            Map.Entry::getValue));
            LOG.infof("Loaded %d greetings", greetings.size());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load greeting dataset", e);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }

    record Greeting(String code, String message) { }
}
