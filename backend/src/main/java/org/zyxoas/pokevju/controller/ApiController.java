package org.zyxoas.pokevju.controller;

import com.jayway.jsonpath.JsonPath;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
@Slf4j
public class ApiController {

    @Value("${pokevju.pokeapi.url}")
    private String pokeapiUrl;

    @Value("${pokevju.homesprites.url.format}")
    private String spriteUrlFormat;

    private static boolean namesFetched = false;

    private static final Map<String, Integer> pokeIdMap = new HashMap<>();

    @GetMapping("/allnames")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieve all Pokémon names and associated ID's", tags = {"pokemon"})
    public List<Map<String, String>> getNames() {
        if (!namesFetched) {
            fetchNames();
        }

        return pokeIdMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        Map.of(
                                "name", StringUtils.capitalize(entry.getKey()),
                                "id", entry.getValue().toString()
                        )
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/sprite/{name}")
    @Produces({org.springframework.http.MediaType.IMAGE_PNG_VALUE})
    @Operation(summary = "Redirect to Pokémon's sprite URL", tags = {"pokemon"})
    public void getPictureUrlByName(HttpServletResponse response, @PathVariable("name") String name) throws IOException {
        if (!namesFetched) {
            fetchNames();
        }

        final Integer id = pokeIdMap.get(name);

        if (id == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {

            final byte[] apiResponse = WebClient
                    .create(String.format(spriteUrlFormat, id))
                    .get()
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            response.setContentType(org.springframework.http.MediaType.IMAGE_PNG_VALUE);

            assert apiResponse != null;
            StreamUtils.copy(apiResponse, response.getOutputStream());
        }
    }

    @PostConstruct
    private void fetchNames() {
        final int limit = 2000;

        log.info("Fetching Pokémon names and ID's the first time.");

        final String apiResponse = WebClient
                .create(pokeapiUrl + "/pokemon/?offset=0&limit=" + limit)
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONArray pikamap = JsonPath
                .parse(apiResponse)
                .read("$.results.*");

        pikamap.forEach(o -> {
            final Map<String, String> input = (Map<String, String>) o;

            String name = "", url = "";

            for (final Map.Entry<String, String> entry : input.entrySet()) {
                switch (entry.getKey()) {
                    case "name":
                        name = entry.getValue();
                        break;
                    case "url":
                        url = entry.getValue();
                        break;
                    default:
                        throw new RuntimeException("Unexpected tag: " + entry.getKey());
                }
            }

            if (!name.contains("-")) {
                final String[] urlSplit = url.split("/");
                final int id = Integer.parseInt(urlSplit[urlSplit.length - 1]);

                pokeIdMap.put(name, id);
            }
        });


        namesFetched = true;

        log.info("Done fetching {} Pokémon names and ID's.", pokeIdMap.size());
    }
}
