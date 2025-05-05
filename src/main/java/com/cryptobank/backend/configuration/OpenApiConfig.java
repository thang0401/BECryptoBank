package com.cryptobank.backend.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${GIT_COMMIT_ID_FULL:}")
    private String commitId;

    @Value("${GIT_COMMIT_MESSAGE_FULL:}")
    private String commitMessage;

    @Value("${GIT_COMMIT_TIME:}")
    private String commitTime;

    @Value("${GIT_COMMIT_AUTHOR_NAME:}")
    private String commitAuthorName;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("CryptoBank API").version("1.0");
        if (!commitId.isBlank() && !commitMessage.isBlank() && !commitTime.isBlank() && !commitAuthorName.isBlank()) {
            info = info.description(
                "<p><b>Commit SHA</b>: " + commitId + "</p>" +
                "<p><b>Commit message:</b> " + commitMessage + "</p>" +
                "<p><b>Commit time:</b> " + commitTime + "</p>" +
                "<p><b>Commit author:</b> " + commitAuthorName + "</p>");
        }
        return new OpenAPI()
            .info(info)
            .servers(List.of(
                new Server().url("https://be-crypto-depot.name.vn"),
                new Server().url("http://localhost:8000")))
            .components(new Components().addSecuritySchemes("Bearer Authorization", new SecurityScheme()
                .name("Bearer Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
    }

}
