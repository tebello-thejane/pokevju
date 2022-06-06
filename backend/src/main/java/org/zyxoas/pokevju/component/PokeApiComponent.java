package org.zyxoas.pokevju.component;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Profile("!test")
@Slf4j
public class PokeApiComponent implements ApiComponent {
    @Value("${pokevju.homesprites.url.format}")
    private String spriteUrlFormat;

    @Value("${pokevju.pokeapi.url}")
    private String pokeapiUrl;

    private static final Map<String, Integer> pokeIdMap = new HashMap<>();

    @Override
    public byte[] getSprite(Integer id) {
        return WebClient
                .create(String.format(spriteUrlFormat, id))
                .get()
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    @Override
    public Map<String, Integer> getIdMap() {
        return pokeIdMap;
    }

    @PostConstruct
    private void fetchNames() {
        final int limit = 2000;

        //Do this asynchronously (fire & forget) so that it does not block booting
        Executors.newSingleThreadExecutor().submit(() -> {

            log.info("Fetching Pokémon names and ID's the first time.");

            final String apiResponse = WebClient
                    .create(pokeapiUrl + "/pokemon/?offset=0&limit=" + limit)
                    .get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            final JSONArray pokamap = JsonPath
                    .parse(apiResponse)
                    .read("$.results.*");

            pokamap.forEach(o -> {
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

            log.info("Done fetching {} Pokémon names and ID's.", pokeIdMap.size());
        });
    }
}
