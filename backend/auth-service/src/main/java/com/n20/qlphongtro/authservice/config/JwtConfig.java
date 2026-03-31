package com.n20.qlphongtro.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String accessTokenSecret;
    private String refreshTokenSecret;
    private Integer accessTokenExpireMinutes;
    private Integer refreshTokenExpireDays;
}
