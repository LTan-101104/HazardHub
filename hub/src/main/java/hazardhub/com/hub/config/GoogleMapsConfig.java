package hazardhub.com.hub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GoogleMapsConfig {

    @Value("${google.maps.api.key:}")
    private String apiKey;

    @Bean
    public RestClient googleMapsRestClient() {
        return RestClient.builder()
                .baseUrl("https://maps.googleapis.com")
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }
}
