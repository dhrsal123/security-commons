package io.cinema.config;

import io.cinema.domain.dto.Cors;
import io.cinema.domain.dto.OAuth2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "security.commons")
public class AppProperties {
    private Cors cors;
    private OAuth2 oAuth2;
}
