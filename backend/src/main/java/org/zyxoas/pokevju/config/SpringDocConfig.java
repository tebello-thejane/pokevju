package org.zyxoas.pokevju.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI pokevjuOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PokéVju API")
                        .description("PokéVju backend application")
                        .version("v0.0.1")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
