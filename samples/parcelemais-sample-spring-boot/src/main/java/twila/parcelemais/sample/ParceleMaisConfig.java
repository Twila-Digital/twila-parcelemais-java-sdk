package twila.parcelemais.sample;

import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParceleMaisConfig {

    @Bean(destroyMethod = "close")
    public ParceleMaisClient parceleMaisClient(
            @Value("${parcelemais.client-id}") String clientId,
            @Value("${parcelemais.client-secret}") String clientSecret,
            @Value("${parcelemais.environment:STAGING}") ParceleMaisEnvironment environment) {
        return ParceleMaisClient.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(environment)
                .build();
    }
}
