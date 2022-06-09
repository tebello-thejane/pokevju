package org.zyxoas.pokevju.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.zyxoas.pokevju.component.ApiComponent;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequestMapping("/api/v1") //<< API versioning
@RestController
@Slf4j
public class ApiController {

    @Autowired
    private ApiComponent apiComponent;

    @Value("${pokevju.frontend.domain.url}")
    private String frontendDomain;

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(frontendDomain);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @GetMapping("/allnames")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve all Pokémon names and associated ID's", tags = {"pokemon"})
    public List<Map<String, String>> getNames() {

        return apiComponent.getIdMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        Map.of(
                                "name", StringUtils.capitalize(entry.getKey()),
                                "id", entry.getValue().toString()
                        )
                )
                .collect(Collectors.toList());
    }

    final LoadingCache<Integer, ApiComponent.TaggedImage> imgCache = CacheBuilder.newBuilder()
            .maximumSize(20)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public ApiComponent.TaggedImage load(Integer id) {
                    return apiComponent.getSprite(id);
                }
            });

    @GetMapping("/sprite/{name}")
    @Produces({"image/svg+xml, image/png"})
    @Operation(summary = "Redirect to Pokémon's sprite URL", tags = {"pokemon"})
    public void getPictureUrlByName(
            HttpServletResponse response,
            @PathVariable("name") String name
    ) throws IOException, ExecutionException {
        final Integer id = apiComponent.getIdMap().get(name);

        if (id == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {

            final ApiComponent.TaggedImage apiResponse = imgCache.get(id);

            response.setContentType(apiResponse.getMediaType());

            StreamUtils.copy(apiResponse.getContents(), response.getOutputStream());
        }
    }
}
