package org.zyxoas.pokevju.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jayway.jsonpath.JsonPath;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
import org.zyxoas.pokevju.component.ApiComponent;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
@Slf4j
public class ApiController {

    @Autowired
    private ApiComponent apiComponent;

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

    final LoadingCache<Integer, byte[]> imgCache = CacheBuilder.newBuilder()
            .maximumSize(20)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public byte[] load(Integer id) {
                    return apiComponent.getSprite(id);
                }
            });

    @GetMapping("/sprite/{name}")
    @Produces("image/svg+xml")
    @Operation(summary = "Redirect to Pokémon's sprite URL", tags = {"pokemon"})
    public void getPictureUrlByName(
            HttpServletResponse response,
            @PathVariable("name") String name
    ) throws IOException, ExecutionException {
        final Integer id = apiComponent.getIdMap().get(name);

        if (id == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {

            final byte[] apiResponse = imgCache.get(id);

            response.setContentType("image/svg+xml");

            StreamUtils.copy(apiResponse, response.getOutputStream());
        }
    }
}
